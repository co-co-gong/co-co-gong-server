package com.server.domain.friend.dto;

import java.util.UUID;

import com.server.domain.friend.enums.FriendState;

import lombok.Getter;

@Getter
public class FriendListDto {
    private UUID id;
    private UUID requestUserId;
    private UUID receiptUserId;
    private FriendState state;
}
