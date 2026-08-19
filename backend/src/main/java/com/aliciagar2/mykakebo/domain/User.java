package com.aliciagar2.mykakebo.domain;

public record User(
        Long id,
        String email,
        String passwordHash,
        String salt
) {
}