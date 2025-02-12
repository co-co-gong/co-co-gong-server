package com.server.domain.friend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.domain.friend.entity.FriendList;
import com.server.domain.user.entity.User;

@Repository
public interface FriendListRepository extends JpaRepository<FriendList, Long> {

    Optional<List<FriendList>> findByRequestUser(User requestUser);

    Optional<List<FriendList>> findByReceiptUser(User receiptUser);

    Optional<FriendList> findByRequestUserAndReceiptUser(User requestUser, User receiptUser);
}
