package com.example.userapi.service;

import com.example.userapi.dto.*;
import com.example.userapi.exception.UserNotFoundException;
import com.example.userapi.model.*;
import com.example.userapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(UserRequest request) {
        return toResponse(userRepository.save(toEntity(request)));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        return userRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public UserResponse updateUser(Long id, UserRequest request) {
        return userRepository.findById(id).map(existing -> {
            existing.setName(request.getName());
            existing.setUsername(request.getUsername());
            existing.setEmail(request.getEmail());
            existing.setPhone(request.getPhone());
            existing.setWebsite(request.getWebsite());
            existing.setAddress(toAddressEntity(request.getAddress()));
            existing.setCompany(toCompanyEntity(request.getCompany()));
            return toResponse(userRepository.save(existing));
        }).orElseThrow(() -> new UserNotFoundException(id));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) throw new UserNotFoundException(id);
        userRepository.deleteById(id);
    }

    // --- Mapping helpers ---

    private User toEntity(UserRequest r) {
        User u = new User();
        u.setName(r.getName());
        u.setUsername(r.getUsername());
        u.setEmail(r.getEmail());
        u.setPhone(r.getPhone());
        u.setWebsite(r.getWebsite());
        u.setAddress(toAddressEntity(r.getAddress()));
        u.setCompany(toCompanyEntity(r.getCompany()));
        return u;
    }

    private UserResponse toResponse(User u) {
        UserResponse res = new UserResponse();
        res.setId(u.getId());
        res.setName(u.getName());
        res.setUsername(u.getUsername());
        res.setEmail(u.getEmail());
        res.setPhone(u.getPhone());
        res.setWebsite(u.getWebsite());
        if (u.getAddress() != null) {
            AddressDto a = new AddressDto();
            a.setStreet(u.getAddress().getStreet());
            a.setSuite(u.getAddress().getSuite());
            a.setCity(u.getAddress().getCity());
            a.setZipcode(u.getAddress().getZipcode());
            if (u.getAddress().getGeo() != null) {
                a.setGeo(new GeoDto(u.getAddress().getGeo().getLat(), u.getAddress().getGeo().getLng()));
            }
            res.setAddress(a);
        }
        if (u.getCompany() != null) {
            CompanyDto c = new CompanyDto();
            c.setName(u.getCompany().getName());
            c.setCatchPhrase(u.getCompany().getCatchPhrase());
            c.setBs(u.getCompany().getBs());
            res.setCompany(c);
        }
        return res;
    }

    private Address toAddressEntity(AddressDto dto) {
        if (dto == null) return null;
        Address a = new Address();
        a.setStreet(dto.getStreet());
        a.setSuite(dto.getSuite());
        a.setCity(dto.getCity());
        a.setZipcode(dto.getZipcode());
        if (dto.getGeo() != null) {
            Geo g = new Geo();
            g.setLat(dto.getGeo().getLat());
            g.setLng(dto.getGeo().getLng());
            a.setGeo(g);
        }
        return a;
    }

    private Company toCompanyEntity(CompanyDto dto) {
        if (dto == null) return null;
        Company c = new Company();
        c.setName(dto.getName());
        c.setCatchPhrase(dto.getCatchPhrase());
        c.setBs(dto.getBs());
        return c;
    }
}
