package com.app.miliwili.src.exercise;

import com.app.miliwili.src.exercise.model.ExerciseInfo;
import com.app.miliwili.src.exercise.model.QExerciseInfo;
import com.app.miliwili.src.user.models.User;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExerciseSelectRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public ExerciseSelectRepository(JPAQueryFactory queryFactory){
        super(User.class);
        this.queryFactory=queryFactory;
    }

    /**
     * first-weight에서 등록되어 있는 운동인지 파악
     * exerciseInfo 가져오기
     */
    public List<Long> getExerciseInfoByUserId(Long userId){
        QExerciseInfo exerciseInfo = QExerciseInfo.exerciseInfo;
        return queryFactory.select((Projections.constructor(Long.class,
                exerciseInfo.id)))
                .from(exerciseInfo)
                .where(exerciseInfo.user.id.eq(userId), exerciseInfo.status.eq("Y"))
                .fetch();

    }


}
