package com.kakaoseventeen.dogwalking.match.repository;

import com.kakaoseventeen.dogwalking.match.domain.Match;
import com.kakaoseventeen.dogwalking.walk.domain.Walk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MatchingRepository extends JpaRepository<Match, Long> {

    /**
     * MatchId를 통해서 Match 엔티티와 연관된 알바 엔티티, Notification 엔티티를 join fetch로 가져오는 쿼리
     */
    @Query("select m from Match m join fetch m.applicationId join fetch m.notificationId where m.matchId = :matchId")
    Match findMatchById(Long matchId);

}