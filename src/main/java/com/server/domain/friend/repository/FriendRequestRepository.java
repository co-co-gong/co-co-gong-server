package com.server.domain.friend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.domain.friend.entity.FriendRequest;
import com.server.domain.friend.enums.FriendRequestState;
import com.server.domain.user.entity.User;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    Optional<List<FriendRequest>> findByRequestUser(User requestUser);

    Optional<List<FriendRequest>> findByReceiptUser(User receiptUser);

    Optional<FriendRequest> findByRequestUserAndReceiptUser(User requestUser, User receiptUser);

    Optional<List<FriendRequest>> findByRequestUserAndState(User requestUser, FriendRequestState state);

    Optional<List<FriendRequest>> findByReceiptUserAndState(User receiptUser, FriendRequestState state);

    void deleteById(Long id);
}
