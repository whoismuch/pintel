package com.pintel.service;

import com.pintel.exception.TgUserNotFoundException;
import com.pintel.model.TgUser;
import com.pintel.repository.TgUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TgUserService {

    private final TgUserRepository userRepository;

    public void addUser(Long userId, String username, String chatId, String lastMessage) {
        userRepository.save(TgUser.builder()
                .userId(userId)
                .username(username)
                .chatId(chatId)
                .lastCommand(lastMessage)
                .build());
    }

    public void saveSelectionType(Long userId, String message) {
        Optional<TgUser> possibleUser = userRepository.findById(userId);
        if (possibleUser.isPresent()) {
            TgUser user = possibleUser.get();
            user.setSelectionType(message);
            userRepository.save(user);
        } else {
            throw new TgUserNotFoundException(userId.toString());
        }
    }

    public void saveLastCommand(Long userId, String message) throws TgUserNotFoundException {
        Optional<TgUser> possibleUser = userRepository.findById(userId);
        if (possibleUser.isPresent()) {
            TgUser user = possibleUser.get();
            user.setLastCommand(message);
            userRepository.save(user);
        } else {
            throw new TgUserNotFoundException(userId.toString());
        }
    }

    public String getSelectionType(Long userId) throws TgUserNotFoundException {
        Optional<TgUser> possibleUser = userRepository.findById(userId);
        if (possibleUser.isPresent()) {
            return possibleUser.get().getSelectionType();
        } else {
            throw new TgUserNotFoundException(userId.toString());
        }
    }

    public String getLastCommand(Long userId) throws TgUserNotFoundException {
        Optional<TgUser> possibleUser = userRepository.findById(userId);
        if (possibleUser.isPresent()) {
            return possibleUser.get().getLastCommand();
        } else {
            throw new TgUserNotFoundException(userId.toString());
        }
    }
}
