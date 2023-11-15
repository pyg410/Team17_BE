package com.kakaoseventeen.dogwalking.application.repository;

import com.kakaoseventeen.dogwalking.application.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Application (지원서) 레파지토리
 *
 * @author 박영규
 * @version 1.0
 */
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    /**
     * 유저 아이디로 지원서를 조회하는 쿼리
     */
    @Query("select a " +
            "from Application a " +
            "where a.appMemberId.id =:userId")
    List<Application> findApplicationByMemberId(Long userId);

}
