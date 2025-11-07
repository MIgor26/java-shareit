package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository requestRepository;

    @Override
    @Transactional
    public ItemRequestDtoOut add(Long userId, ItemRequestDto itemRequestDto) {
        User user = UserMapper.toUser(userService.findById(userId));
        ItemRequest request = ItemRequestMapper.toRequest(user, itemRequestDto);
        ItemRequestDtoOut requestDtoOut = ItemRequestMapper.toRequestDtoOut(requestRepository.save(request));
        log.info("Запрос {} успешно добавлен", requestDtoOut);
        return requestDtoOut;
    }

    @Override
    public List<ItemRequestDtoOut> getUserRequests(Long userId) {
        userService.findById(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequestList = requestRepository.findAllByRequesterId(userId, sort);
        log.info("Для пользователя с id = {} найдено {} запросов", userId, itemRequestList.size());
        return itemRequestList.stream()
                .map(ItemRequestMapper::toRequestDtoOut)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoOut> getAllRequests(Long userId, Integer from, Integer size) {
        userService.findById(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequestList = requestRepository.findAllByRequesterIdIsNot(userId, sort);
        log.info("Для пользователя с id = {} найдено {} запросов других пользователей", userId, itemRequestList.size());
        return itemRequestList.stream()
                .map(ItemRequestMapper::toRequestDtoOut)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoOut getRequestById(Long userId, Long requestId) {
        userService.findById(userId);
        Optional<ItemRequest> requestById = requestRepository.findById(requestId);
        if (requestById.isEmpty()) {
            throw new NotFoundException(String.format("Запрос с id: %s не был найден.", requestId));
        }
        ItemRequestDtoOut requestDtoOut = ItemRequestMapper.toRequestDtoOut(requestById.get());
        log.info("Запрос по id = {} найден: {}", requestId, requestDtoOut);
        return requestDtoOut;
    }
}

