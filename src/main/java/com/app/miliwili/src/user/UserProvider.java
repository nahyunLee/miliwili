package com.app.miliwili.src.user;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.src.main.dto.UserMainData;
import com.app.miliwili.src.user.dto.GetAbnormalUserEndDate;
import com.app.miliwili.src.user.dto.UserRes;
import com.app.miliwili.src.user.dto.VacationRes;
import com.app.miliwili.src.user.dto.VacationSelectData;
import com.app.miliwili.src.user.models.UserInfo;
import com.app.miliwili.src.user.models.Vacation;
import com.app.miliwili.utils.JwtService;
import com.app.miliwili.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class UserProvider {
    private final UserRepository userRepository;
    private final UserSelectRepository userSelectRepository;
    private final VacationRepository vacationRepository;
    private final VacationSelectRepository vacationSelectRepository;
    private final JwtService jwtService;


    /**
     * 로그인시 존재하는 구글 아이디(socialId) 검사
     */
    public List<Long> isGoogleUser(String gSocialId) throws BaseException {
        List<Long> userList = null;

        try {
            userList = userSelectRepository.findUsersIdByGoogleId(gSocialId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(FAILED_TO_GET_USER);
        }


        return userList;
    }

    /**
     * stateIdx --> 일반병사인지 아닌지 확인
     *
     * @Auther vivi
     */
    public int retrieveUserstateIdx(long id) throws BaseException {
        int userStateIdx = 0;
        try {
            List<Integer> userStateList = userSelectRepository.findUserStateIdxByUserId(id);
            if (userStateList.size() == 0)
                throw new BaseException(FAILED_TO_GET_USER);

            userStateIdx = userStateList.get(0);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(FAILED_TO_GET_USER);
        }

        return userStateIdx;
    }

    /**
     * for 전역일 계산기 (MainProvider)
     * [ 부사관, 준사관, 장교 ]들을 위한
     *
     * @return GetAbnormalUserEndDate
     * @Auther vivi
     */
    public GetAbnormalUserEndDate retrieveAbnormalUserEndDate(long id) throws BaseException {
        GetAbnormalUserEndDate abnormalUserEndDate;
        try {
            List<GetAbnormalUserEndDate> userList = userSelectRepository.findEndDateInfoByUserId(id);

            if (userList.size() == 0)
                throw new BaseException(FAILED_TO_GET_USER);

            abnormalUserEndDate = userList.get(0);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(FAILED_TO_GET_USER);
        }

        return abnormalUserEndDate;
    }

    /**
     * userId로 유효한 회원조회
     *
     * @param userId
     * @return User
     * @throws BaseException
     * @Auther shine
     */
    public UserInfo retrieveUserByIdAndStatusY(Long userId) throws BaseException {
        return userRepository.findByIdAndStatus(userId, "Y")
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
    }

    /**
     * socialId로 유효한 회원 존재여부 체크
     * (존재하면 true, 존재하지 않으면 false)
     *
     * @param socialId
     * @return boolean
     * @Auther shine
     */
    public boolean isUserBySocialId(String socialId) {
        return userRepository.existsBySocialIdAndStatus(socialId, "Y");
    }

    /**
     * socialId로 유효한 회원조회
     *
     * @param socialId
     * @return User
     * @throws BaseException
     * @Auther shine
     */
    public UserInfo retrieveUserBySocialIdAndStatusY(String socialId) throws BaseException {
        return userRepository.findBySocialIdAndStatus(socialId, "Y")
                .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
    }

    /**
     * vacationId로 휴가조회
     *
     * @param vacationId
     * @return Vacation
     * @throws BaseException
     * @Auther shine
     */
    public Vacation retrieveVacationById(Long vacationId) throws BaseException {
        return vacationRepository.findById(vacationId)
                .orElseThrow(() -> new BaseException(NOT_FOUND_VACATION));
    }




    /**
     * 내 휴가 전체조회
     *
     * @return List<VacationRes>
     * @throws BaseException
     * @Auther shine
     */
    public List<VacationRes> getVacation() throws BaseException {
        try {
            List<VacationSelectData> vacations = vacationSelectRepository.findVacationByUserIdAndStatusY(jwtService.getUserId());

            if (vacations.isEmpty()) {
                throw new BaseException(THIS_USER_NOT_FOUND_VACATION);
            }

            return vacations.stream().map(vacationSelectDate -> {
                return VacationRes.builder()
                        .vacationId(vacationSelectDate.getId())
                        .title(vacationSelectDate.getTitle())
                        .useDays(vacationSelectDate.getUseDays() + Validation.isInteger(vacationSelectDate.getCount()))
                        .totalDays(vacationSelectDate.getTotalDays())
                        .build();
            }).collect(Collectors.toList());
        } catch (BaseException exception) {
            if (exception.getStatus() == THIS_USER_NOT_FOUND_VACATION) {
                throw new BaseException(THIS_USER_NOT_FOUND_VACATION);
            }
            throw new BaseException(FAILED_TO_GET_VACATION);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_GET_VACATION);
        }
    }

    /**
     * 내 회원정보 조회
     *
     * @return UserRes
     * @throws BaseException
     * @Auther shine
     */
    public UserRes getUser() throws BaseException {
        try {
            UserInfo user = retrieveUserByIdAndStatusY(jwtService.getUserId());
            return changeUserInfoToUserRes(user);
        } catch (BaseException exception) {
            if (exception.getStatus() == NOT_FOUND_USER) {
                throw new BaseException(NOT_FOUND_USER);
            }
            throw new BaseException(FAILED_TO_GET_USER);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_GET_USER);
        }
    }

    /**
     * 메인에 노출할 내 회원정보 조회
     *
     * @return UserMainData
     * @throws BaseException
     */
    public UserMainData retrieveUserMainDataById() throws BaseException{
        try{
            UserMainData userMainData = userSelectRepository.findUserMainDataByUserId(jwtService.getUserId());

            if (Objects.isNull(userMainData)) {
                throw new BaseException(NOT_FOUND_MAIN_INFO);
            }
            return userMainData;
        } catch (BaseException exception) {
            if(exception.getStatus() == NOT_FOUND_MAIN_INFO) {
                throw new BaseException(NOT_FOUND_MAIN_INFO);
            }
            throw new BaseException(FAILED_TO_GET_USER_MAIN_INFO);
        } catch (Exception exception) {
            throw new BaseException(FAILED_TO_GET_USER_MAIN_INFO);
        }
    }




    /**
     * UserInfo -> UserRes 변경
     *
     * @param user
     * @return UserRes
     * @Auther shine
     */
    public UserRes changeUserInfoToUserRes(UserInfo user) {
        if (Objects.isNull(user.getNormalPromotionState())) {
            return UserRes.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .birthday(Validation.isLocalDateAndChangeString(user.getBirthday()))
                    .profileImg(user.getProfileImg())
                    .stateIdx(user.getStateIdx())
                    .serveType(user.getServeType())
                    .startDate(user.getStartDate().format(DateTimeFormatter.ISO_DATE))
                    .endDate(user.getEndDate().format(DateTimeFormatter.ISO_DATE))
                    .proDate(user.getAbnormalPromotionState().getProDate().format(DateTimeFormatter.ISO_DATE))
                    .goal(user.getGoal())
                    .build();
        }
        return UserRes.builder()
                .userId(user.getId())
                .name(user.getName())
                .birthday(Validation.isLocalDateAndChangeString(user.getBirthday()))
                .profileImg(user.getProfileImg())
                .stateIdx(user.getStateIdx())
                .serveType(user.getServeType())
                .startDate(user.getStartDate().format(DateTimeFormatter.ISO_DATE))
                .endDate(user.getEndDate().format(DateTimeFormatter.ISO_DATE))
                .strPrivate(user.getNormalPromotionState().getFirstDate().format(DateTimeFormatter.ISO_DATE))
                .strCorporal(user.getNormalPromotionState().getSecondDate().format(DateTimeFormatter.ISO_DATE))
                .strSergeant(user.getNormalPromotionState().getThirdDate().format(DateTimeFormatter.ISO_DATE))
                .hobong(user.getNormalPromotionState().getHobong())
                .normalPromotionStateIdx(user.getNormalPromotionState().getStateIdx())
                .goal(user.getGoal())
                .build();
    }
}