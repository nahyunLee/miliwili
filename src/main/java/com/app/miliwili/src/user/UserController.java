package com.app.miliwili.src.user;

import com.app.miliwili.config.BaseException;
import com.app.miliwili.config.BaseResponse;
import com.app.miliwili.src.user.models.*;
import com.app.miliwili.utils.SNSLogin;
import com.app.miliwili.utils.JwtService;
import com.app.miliwili.utils.ValidationLength;
import com.app.miliwili.utils.ValidationRegex;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.app.miliwili.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app")
public class UserController {
    private final JwtService jwtService;
    private final SNSLogin SNSLogin;
    private final UserService userService;
    private final UserProvider userProvider;

    /**
     * JWT 검증 API
     * [GET] /app/users/jwt
     * @RequestHeader X-ACCESS-TOKEN
     * @return BaseResponse<Void>
     * @Auther shine
     */
    @GetMapping("/users/jwt")
    public BaseResponse<Void> jwt(@RequestHeader("X-ACCESS-TOKEN") String jwt) {
        try {
            Long userId = jwtService.getUserId();
            List<User> user = userProvider.retrieveUserByIdAndStatusY(userId);
            if (user == null || user.isEmpty()) {
                return new BaseResponse<>(NOT_FOUND_USER);
            }
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 로그인 --> 구글
     * [POST]
     * @RequestHeader token(google accessToken)
     * @return BaseResponse<PostLoginRes>
     * @Auther vivi
     */
    @ResponseBody
    @PostMapping("/users/login-google")
    public BaseResponse<PostLoginRes> postLoginGoogle(@RequestHeader("X-ACCESS-TOKEN") String token){

        String socialId="";
        try {
             socialId = SNSLogin.userIdFromGoogle(token);
        }catch (BaseException e){
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }

        try{
            PostLoginRes returnRes = userService.createGoogleJwtToken(socialId);
            return new BaseResponse<>(SUCCESS,returnRes);
        }catch(BaseException e){
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 회원가입 --> 구글
     * @RequestHeader token(google accessToken)
     * @return BaseResponse<PostLoginRes>
     * @Auther vivi
     */
    @ResponseBody
    @PostMapping("/users/google")
    public BaseResponse<PostSignUpRes> postSignUpGoogle(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                        @RequestBody PostSignUpReq param){
        //이름 check
        if(!ValidationLength.isFullString(param.getName())){
            return new BaseResponse<>(EMPTY_NAME);

        }

        //복무형태 check  (일반병사, 부사관, 준사관, 장교)
        if(param.getStateIdx() > 5 || param.getStateIdx() < 1){
            return new BaseResponse<>(INVALID_STATEIDX);
        }

        //하위 복무형태 check(육군, 해군)
        if(!ValidationLength.isFullString(param.getServeType())){
            return new BaseResponse<>(EMPTY_SERVE_TYPE);
        }


        //입대일 check
        if(!ValidationLength.isFullString(param.getStartDate())) {
            return new BaseResponse<>(EMPTY_START_DATE);
        }
        if(!ValidationRegex.isRegexDate(param.getStartDate())){
            return new BaseResponse<>(INVALID_START_DATE);
        }


        //전역일 check
        if(!ValidationLength.isFullString(param.getEndDate())) {
            return new BaseResponse<>(EMPTY_END_DATE);
        }
        if(!ValidationRegex.isRegexDate(param.getEndDate())){
            return new BaseResponse<>(INVALID_END_DATE);
        }



        if(param.getStateIdx() == 1){   //일반병사일 떄
            if(!ValidationLength.isFullString(param.getStrPrivate())) {
                return new BaseResponse<>(EMPTY_FIRST_DATE);
            }
            if(!ValidationRegex.isRegexDate(param.getStrPrivate())){
                return new BaseResponse<>(INVALID_END_DATE);
            }

            if(!ValidationLength.isFullString(param.getStrCorporal())) {
                return new BaseResponse<>(EMPTY_SECOND_DATE);
            }
            if(!ValidationRegex.isRegexDate(param.getStrCorporal())){
                return new BaseResponse<>(INVALID_SECOND_DATE);
            }

            if(!ValidationLength.isFullString(param.getStrSergeant())) {
                return new BaseResponse<>(EMPTY_THIRD_DATE);
            }
            if(!ValidationRegex.isRegexDate(param.getStrSergeant())){
                return new BaseResponse<>(INVALID_THIRD_DATE);
            }

        }else{      //일반병사 아닐 때
            if(ValidationLength.isFullString(param.getProDate())) {
                if (!ValidationRegex.isRegexDate(param.getProDate())) {
                    return new BaseResponse<>(INVALID_PRO_DATE);
                }
            }
        }
        String socialId="";
        try {
            socialId = SNSLogin.userIdFromGoogle(token);
        }catch (BaseException e){
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }

        try{
            PostSignUpRes postSignUpRes = userService.createGoogleUser(param,socialId);
            return new BaseResponse<>(SUCCESS, postSignUpRes);
        }catch(BaseException e){
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 카카오 로그인
     * [POST] /app/users/login-kakao
     * @RequestHeader X-ACCESS-TOKEN
     * @return BaseResponse<PostLoginRes>
     * @Auther shine
     */
    @ResponseBody
    @PostMapping("/users/login-kakao")
    public BaseResponse<PostLoginRes> postLoginKakao(@RequestHeader("X-ACCESS-TOKEN") String token) {
        try {
            PostLoginRes postLoginRes = userService.login(SNSLogin.userIdFromKakao(token));
            return new BaseResponse<>(SUCCESS, postLoginRes);
        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 카카오 회원가입
     * [POST] /app/users/kakao
     * @RequestHeader X-ACCESS-TOKEN
     * @return BaseResponse<Void>
     * @Auther shine
     */
    @ResponseBody
    @PostMapping("/users/kakao")
    public BaseResponse<PostSignUpRes> postSignUpKakao(@RequestHeader("X-ACCESS-TOKEN") String token,
                                                       @RequestBody(required = false) PostSignUpReq parameters) {
        // TODO 유효성 검사
        try {
            PostSignUpRes postSignUpRes = userService.createUser(parameters, SNSLogin.userIdFromKakao(token), "K", token);
            return new BaseResponse<>(SUCCESS, postSignUpRes);
        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 테스트용 jwt 생성, 나중에 삭제할거
     * [post] /api/jwt
     */
    @PostMapping("/jwt/{id}")
    public String postJWT(@PathVariable Long id) {
        return jwtService.createJwt(id);
    }
}