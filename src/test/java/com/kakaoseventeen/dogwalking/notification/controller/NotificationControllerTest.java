package com.kakaoseventeen.dogwalking.notification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaoseventeen.dogwalking.dog.domain.Dog;
import com.kakaoseventeen.dogwalking.dog.repository.DogRepository;
import com.kakaoseventeen.dogwalking.member.domain.Member;
import com.kakaoseventeen.dogwalking.member.repository.MemberRepository;
import com.kakaoseventeen.dogwalking.notification.domain.Notification;
import com.kakaoseventeen.dogwalking.notification.dto.request.WriteNotificationReqDTO;
import com.kakaoseventeen.dogwalking.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * 강아지 불러오기 & 공고 상세페이지 테스트
 *
 * @author 곽민주
 * @version 1.0
 */
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class NotificationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    DogRepository dogRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp() {
        Member master1 = memberRepository.save(Member.builder()
                .id(1L)
                .nickname("닉네임1")
                .email("mkwak1125@gmail.com")
                .password(passwordEncoder.encode("kwak!6038"))
                .profileImage("1번 이미지")
                .profileContent("나는 1번 멤버")
                .coin(BigDecimal.valueOf(100000))
                .build());



        Member master2 = memberRepository.save(Member.builder()
                .id(2L)
                .nickname("닉네임2")
                .email("asfd@gmail.com")
                .password(passwordEncoder.encode("kwak!6038"))
                .profileContent("나는 2번 멤버")
                .profileImage("2번 이미지")
                .dogBowl(80)
                .coin(BigDecimal.valueOf(500000))
                .dogBowl(55)
                .build());

        memberRepository.saveAndFlush(master1);
        memberRepository.saveAndFlush(master2);


        Dog dog1 = Dog.builder()
                .breed("푸들")
                .name("강아지이름1")
                .image("이미지1")
                .sex("수컷")
                .size("대형견")
                .member(master1)
                .build();
        dogRepository.saveAndFlush(dog1);

        Notification notification1 = Notification.builder()
                .dog(dog1)
                .title("제목1")
                .lat(34.25)
                .lng(43.1)
                .coin(BigDecimal.valueOf(40000))
                .startTime(LocalDateTime.of(2023, Month.OCTOBER, 13, 22, 36))
                .endTime(LocalDateTime.of(2023, Month.OCTOBER, 13, 23, 36))
                .significant("우리 아이는 착해용")
                .build();
        notificationRepository.saveAndFlush(notification1);

    }




    @DisplayName("공고 상세페이지 불러오기 테스트 - 성공")
    @WithUserDetails(value = "mkwak1125@gmail.com", userDetailsServiceBeanName = "customUserDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void load_notification_success_test() throws Exception{
        // given
		int id = 1;


        ResultActions resultActions = mvc.perform(
                get(String.format("/api/notification/%d", id))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // console
        String responseBody = new String(resultActions.andReturn().getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
        System.out.println("테스트 : " + responseBody);

        // verify
        resultActions.andExpect(jsonPath("$.success").value("true"));
        resultActions.andExpect(jsonPath("$.response.isMine").value("true"));
        resultActions.andExpect(jsonPath("$.response.dog.dogId").value(1));
    }

    @DisplayName("공고 상세페이지 불러오기 테스트 - 실패 (공고글 존재 x)")
    @WithUserDetails(value = "mkwak1125@gmail.com", userDetailsServiceBeanName = "customUserDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void load_notification_fail_test() throws Exception{
        // given
        int id = 99;


        ResultActions resultActions = mvc.perform(
                get(String.format("/api/notification/%d", id))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // console
        String responseBody = new String(resultActions.andReturn().getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
        System.out.println("테스트 : " + responseBody);

        // verify
        resultActions.andExpect(jsonPath("$.success").value("false"));
        resultActions.andExpect(jsonPath("$.error.message").value("해당 공고글이 존재하지 않습니다."));

    }


    @DisplayName("강아지 불러오기 테스트 - 성공")
    @WithUserDetails(value = "mkwak1125@gmail.com", userDetailsServiceBeanName = "customUserDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void load_dog_success_test() throws Exception{

        // when
        ResultActions resultActions = mvc.perform(
                get(String.format("/api/notification"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)

        );

        // console
        String responseBody = new String(resultActions.andReturn().getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
        System.out.println("테스트 : " + responseBody);

        resultActions.andExpect(jsonPath("$.success").value("true"));
        resultActions.andExpect(jsonPath("$.response.dogs[0].dogId").value(1));
        resultActions.andExpect(jsonPath("$.response.dogs[0].dogName").value("강쥐"));
    }

    @DisplayName("강아지 불러오기 테스트 - 실패")
    @WithUserDetails(value = "asfd@gmail.com", userDetailsServiceBeanName = "customUserDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void load_dog_fail_test() throws Exception{

        // when
        ResultActions resultActions = mvc.perform(
                get(String.format("/api/notification"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)

        );

        // console
        String responseBody = new String(resultActions.andReturn().getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
        System.out.println("테스트 : " + responseBody);

        resultActions.andExpect(jsonPath("$.success").value("false"));
    }

    @DisplayName("공고글 작성하기 테스트 - 성공")
    @WithUserDetails(value = "mkwak1125@gmail.com", userDetailsServiceBeanName = "customUserDetailsService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void write_notification_success_test() throws Exception{
        // given
        WriteNotificationReqDTO writeNotificationReqDTO = new WriteNotificationReqDTO();
        writeNotificationReqDTO.setDogId(1L);
        writeNotificationReqDTO.setTitle("제목1");
        writeNotificationReqDTO.setSignificant("우리 아이는 착해용");
        writeNotificationReqDTO.setStart(LocalDateTime.of(2023, Month.OCTOBER, 13, 22, 36));
        writeNotificationReqDTO.setCoin(BigDecimal.valueOf(400));
        writeNotificationReqDTO.setEnd(LocalDateTime.of(2023, Month.OCTOBER, 13, 23, 36));
        writeNotificationReqDTO.setLat(34.25);
        writeNotificationReqDTO.setLng(43.1);

		String requestBody = om.writeValueAsString(writeNotificationReqDTO);
        // when
        ResultActions resultActions = mvc.perform(
                post(String.format("/api/notification"))
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        // console
        String responseBody = new String(resultActions.andReturn().getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
        System.out.println("테스트 : " + responseBody);

        // verify
        resultActions.andExpect(jsonPath("$.success").value("true"));
    }



}