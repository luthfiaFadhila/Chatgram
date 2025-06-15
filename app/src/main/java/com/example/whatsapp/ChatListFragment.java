package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.*;
public class ChatListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatListAdapter adapter;
    private final List<ChatItem> chatItemList = new ArrayList<>();
    private DatabaseReference chatRef, userRef;
    private String currentUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerChatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ChatListAdapter(chatItemList, getContext(), chatItem -> {
            if (isAdded() && getActivity() != null) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("receiverUid", chatItem.getUid());
                intent.putExtra("receiverName", chatItem.getName());
                intent.putExtra("user", new User(chatItem.getUid(), chatItem.getName(), chatItem.getProfileUrl()));
                startActivity(intent);

            }
        });
        recyclerView.setAdapter(adapter);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) return view;
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        chatRef = FirebaseDatabase.getInstance().getReference("chats");
        userRef = FirebaseDatabase.getInstance().getReference("users");

        loadChatUsers();

        View fabAdd = view.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getContext(), ListKontakActivity.class);
                startActivity(intent);
            }
        });
        ImageView toolbarMore = view.findViewById(R.id.toolbarMore);

        toolbarMore.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_toolbar, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_profile) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        String name = currentUser.getDisplayName(); // kalau tidak null
                        String phone = currentUser.getPhoneNumber();
                        String email = currentUser.getEmail();

                        Intent intent = new Intent(requireContext(), ProfileActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("phone", phone);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    }
                    return true;
                } else if (item.getItemId() == R.id.menu_logout) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

        return view;
    }

    private void loadChatUsers() {
        chatItemList.clear();
        Set<String> chatUserIds = new HashSet<>();

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot chatRoom : snapshot.getChildren()) {
                    String roomId = chatRoom.getKey();
                    if (roomId != null && roomId.contains(currentUid)) {
                        String[] uids = roomId.split("_");
                        if (uids.length != 2) continue;
                        String receiverId = uids[0].equals(currentUid) ? uids[1] : uids[0];

                        if (chatUserIds.contains(receiverId)) continue; // Hindari duplikasi
                        chatUserIds.add(receiverId);

                        List<ChatMessage> messages = new ArrayList<>();
                        for (DataSnapshot messageSnap : chatRoom.getChildren()) {
                            ChatMessage msg = messageSnap.getValue(ChatMessage.class);
                            if (msg != null) messages.add(msg);
                        }

                        if (!messages.isEmpty()) {
                            messages.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));
                            ChatMessage lastMsg = messages.get(0);

                            final int[] unreadCount = {0};
                            for (ChatMessage msg : messages) {
                                if (msg.receiverId != null &&
                                        msg.receiverId.equals(currentUid) &&
                                        !msg.isRead) {
                                    unreadCount[0]++;
                                }
                            }

                            userRef.child(receiverId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);
                                    if (user != null) {
                                        user.setUid(snapshot.getKey()); // ⬅️ tambahkan ini agar uid tidak null

                                        ChatItem chatItem = new ChatItem(
                                                user.getUid(),
                                                user.getName(),
                                                user.getProfileUrl(),
                                                lastMsg.message,
                                                formatTime(lastMsg.timestamp),
                                                unreadCount[0]
                                        );
                                        chatItemList.add(chatItem);
                                        adapter.notifyDataSetChanged();
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
