package com.pintel.service;


import com.pintel.model.TgUser;
import com.pintel.repository.UserTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserTagService {

    private final UserTagRepository userTagRepository;

    public Map<Long, List<String>> getMostPopularTagsForUsers () {
        //Получаем список юзеров
        List<Long> userIds = getDistinctUsers();

        //Для каждого юзера получаем список наиболее частых тэгов
        Map<Long, List<String>> userPopularTags = userIds
                .stream()
                .collect(HashMap::new, (m, x)->m.put(x, userTagRepository.findMostPopularTagsByUserId(x, 2L)), HashMap::putAll);

        return userPopularTags;
    }

    public List<Long> getDistinctUsers() {
        return userTagRepository.findAllDistinctUsers();
    }
}
