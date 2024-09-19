## 기본 구조

```bash
src/main/java/com/server
├── CoCoGongApplication.java  # Main application class
├── domain                    # Domain별 package
│   ├── user                  # User domain
│   │   ├── controller        # User domain 관련 contoller
│   │   ├── dto               # User domain 관련 DTO
│   │   ├── entity            # Order domain 관련 JPA entity
│   │   ├── repository        # Order domain 관련 repository
│   │   └── service           # Order domain 관련 service
│   └── session               # Session domain
│       ├── controller        # Session domain 관련 controller
│       ├── dto               # Session domain 관련 DTO
│       ├── entity            # Session domain 관련 JPA entity
│       ├── repository        # Session domain 관련 repository
│       └── service           # Session domain 관련 service
└── global                    # Application 전역에서 사용되는 공통 코드
    ├── config                # Global config classes
    ├── exception             # Global exception classes
    └── util                  # Global utility classes
```

## Domain 별 구조

```bash
src/main/java/com/server/domain/user
├── controller               # User 관련 API 요청 처리
│   └── UserController.java  # User API endpoint 정의
├── dto                      # DTO class 정의
│   └── UserDto.java         # User DTO class
├── entity                   # User domain 관련 database table mapping
│   └── User.java            # User JPA entity class
├── repository               # User 관련 data 접근 layer
│   └── UserRepository.java  # User repository interface (JPA)
└── service                  # User 관련 buisness logic
    └── UserService.java     # User service class
```

- `controller`: `controller` 디렉토리에는 REST API 요청을 처리하는 클래스들이 위치합니다. 예를 들어, `UserController.java`는 사용자 관련 요청을 처리하고, 비즈니스 로직은 서비스 레이어로 위임합니다.
- `service`: `service` 디렉토리에는 애플리케이션의 핵심 비즈니스 로직을 담은 서비스 클래스가 있습니다. 예를 들어, `UserService.java`는 사용자 관련 비즈니스 로직을 처리하며, 데이터베이스와의 상호작용은 리포지토리를 통해 수행합니다.
- `repository`: `repository` 디렉토리에는 데이터베이스와 상호작용하는 클래스들이 위치합니다. Spring Data JPA 리포지토리를 사용하여 데이터베이스 CRUD 작업을 수행합니다. 예를 들어, `UserRepository.java`는 JPA를 이용해 사용자 데이터를 관리합니다.
- `entity`: `entity` 디렉토리에는 JPA 엔티티 클래스가 있으며, 데이터베이스 테이블과 매핑되는 객체들이 포함됩니다. 예를 들어, `User.java`는 사용자 테이블과 매핑된 JPA 엔티티 클래스입니다.
- `dto`: `dto` 디렉토리에는 데이터 전송 객체(DTO) 클래스들이 위치합니다. DTO는 컨트롤러와 서비스 간, 또는 클라이언트와 서버 간 데이터 전송에 사용됩니다. 예를 들어, `UserDto.java`는 사용자 데이터의 전송을 위한 객체입니다.

#### `UserController.java`

```java
package com.server.domain.user.controller;

import com.server.domain.user.dto.UserDto;
import com.server.domain.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }
}
```

#### `UserService.java`

```java
package com.server.domain.user.service;

import com.server.domain.user.dto.UserDto;
import com.server.domain.user.entity.User;
import com.server.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(user.getId(), user.getName(), user.getEmail()))
                .collect(Collectors.toList());
    }

    public UserDto createUser(UserDto userDto) {
        User user = new User(userDto.getName(), userDto.getEmail());
        User savedUser = userRepository.save(user);
        return new UserDto(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
    }
}
```

#### `UserRepository.java`

```java
package com.server.domain.user.repository;

import com.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
```

#### `User.java`

```java
package com.server.domain.user.entity;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    // 기본 생성자
    public User() {}

    // 생성자
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getter 및 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
```

#### `UserDto.java`

```java
package com.server.domain.user.dto;

public class UserDto {

    private Long id;
    private String name;
    private String email;

    public UserDto(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getter 및 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
```
