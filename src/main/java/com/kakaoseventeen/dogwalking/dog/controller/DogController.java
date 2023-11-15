package com.kakaoseventeen.dogwalking.dog.controller;

import com.kakaoseventeen.dogwalking._core.security.CustomUserDetails;
import com.kakaoseventeen.dogwalking._core.utils.ApiResponse;
import com.kakaoseventeen.dogwalking._core.utils.ApiResponseGenerator;
import com.kakaoseventeen.dogwalking._core.utils.exception.notification.DogNotExistException;
import com.kakaoseventeen.dogwalking._core.utils.exception.ImageNotExistException;
import com.kakaoseventeen.dogwalking.dog.dto.DogReqDTO;
import com.kakaoseventeen.dogwalking.dog.dto.DogResDTO;
import com.kakaoseventeen.dogwalking.dog.service.DogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DogController {

    private final DogService dogService;

    /**
     * 강아지 프로필 등록 메서드
     */
    @PostMapping(value = "/profile/dog", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<ApiResponse.CustomBody<DogResDTO.save>> saveDog(@ModelAttribute DogReqDTO dogReqDTO,
                                                                        @AuthenticationPrincipal CustomUserDetails customUserDetails) throws ImageNotExistException, IOException {

        DogResDTO.save respDTO = dogService.saveDog(dogReqDTO.getImage(), dogReqDTO, customUserDetails);

        return ApiResponseGenerator.success(respDTO, HttpStatus.OK);
    }

    /**
     * 강아지 프로필 수정 메서드
     */
    @PostMapping(value = "/profile/update/dog/{dogId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<ApiResponse.CustomBody<DogResDTO.save>> updateDog(@PathVariable("dogId") long dogId,
                                                                        @ModelAttribute DogReqDTO dogReqDTO,
                                                                          @AuthenticationPrincipal CustomUserDetails customUserDetails
                                                                        ) throws ImageNotExistException, DogNotExistException, IOException {

        DogResDTO.save respDTO = dogService.updateDog(dogId, dogReqDTO, customUserDetails);

        return ApiResponseGenerator.success(respDTO, HttpStatus.OK);
    }

    /**
     * 강아지 프로필 조회 메서드
     */
    @GetMapping("/profile/dog/{dogId}")
    public ApiResponse<ApiResponse.CustomBody<DogResDTO.findById>> findByDogId(@PathVariable("dogId") long dogId) throws DogNotExistException {
        DogResDTO.findById respDTO = dogService.findByDogId(dogId);
        return ApiResponseGenerator.success(respDTO, HttpStatus.OK);
    }
}
