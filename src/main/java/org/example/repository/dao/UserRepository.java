package org.example.repository.dao;

import org.example.model.User;

import java.util.List;

public interface UserRepository {
    User save(User user);

    User get(int id);

    boolean remove(int id);

    User findByNickname(String nickname);

    boolean changePassword(int userId, String newPassword);

    boolean existsByNicknameAndPassword(String nickname, String password);

    //    @Override
    default User getById(int id) {
        return getById(id);
    }

    User getByNickname(String nickname);

    List<User> findAll();

    List<User> findByRating(int minRating);
}
