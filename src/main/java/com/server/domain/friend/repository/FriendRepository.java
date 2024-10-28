package com.server.domain.friend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.domain.friend.entity.Friend;
import com.server.domain.friend.enums.FriendState;
import com.server.domain.user.entity.User;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    Optional<List<Friend>> findByRequestUser(User requestUser);

    Optional<List<Friend>> findByReceiptUser(User receiptUser);

    Optional<Friend> findByRequestUserAndReceiptUser(User requestUser, User receiptUser);

    Optional<List<Friend>> findByRequestUserAndState(User requestUser, FriendState state);

    Optional<List<Friend>> findByReceiptUserAndState(User receiptUser, FriendState state);
}
