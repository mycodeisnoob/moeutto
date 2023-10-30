package com.ssafy.moeutto.domain.clothes.service;

import com.ssafy.moeutto.domain.clothes.dto.request.ClothesRegistRequestDto;
import com.ssafy.moeutto.domain.clothes.dto.request.ClothesUpdateRequestDto;
import com.ssafy.moeutto.domain.clothes.dto.response.*;
import com.ssafy.moeutto.domain.clothes.entity.Clothes;
import com.ssafy.moeutto.domain.clothes.entity.IClothesAnalysisColor;
import com.ssafy.moeutto.domain.clothes.entity.IClothesAnalysisSeason;
import com.ssafy.moeutto.domain.clothes.repository.ClothesRepository;
import com.ssafy.moeutto.domain.member.entity.Member;
import com.ssafy.moeutto.domain.member.repository.MemberRepository;
import com.ssafy.moeutto.domain.middleCategory.entity.MiddleCategory;
import com.ssafy.moeutto.domain.middleCategory.repository.MiddleCategoryRepository;
import com.ssafy.moeutto.global.response.BaseException;
import com.ssafy.moeutto.global.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClothesServiceImpl implements ClothesService {

    private final ClothesRepository clothesRepository;
    private final MemberRepository memberRepository;
    private final MiddleCategoryRepository middleCategoryRepository;

    /**
     * 옷 정보를 등록합니다.
     *
     * @param clothesRegistRequestDto
     * @return ClothesRegistResponseDto
     * @throws BaseException
     */
    @Override
    public ClothesRegistResponseDto registClothes(ClothesRegistRequestDto clothesRegistRequestDto, UUID memberId) throws BaseException {
        Optional<Member> memberOptional = memberRepository.findById(memberId);

        // 사용자 체크
        if (!memberOptional.isPresent()) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_MEMBER);
        }

        Optional<MiddleCategory> middleCategoryOptional = middleCategoryRepository.findById(clothesRegistRequestDto.getMiddleCategoryId());

        // 중분류 카테고리 체크
        if (!middleCategoryOptional.isPresent()) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_MIDDLE_CATEGORY);
        }

        // 옷 정보 저장
        Clothes clothes = Clothes.builder()
                .member(memberOptional.get())
                .middleCategory(middleCategoryOptional.get())
                .name(clothesRegistRequestDto.getName())
                .season(clothesRegistRequestDto.getSeason())
                .color(clothesRegistRequestDto.getColor())
                .thickness(clothesRegistRequestDto.getThickness())
                .price(clothesRegistRequestDto.getPrice())
                .shop(clothesRegistRequestDto.getShop())
                .textile(clothesRegistRequestDto.getTextile())
                .frequency(0)
                .star(0)
                .regDate(new Date(System.currentTimeMillis()))
                .recentDate(new Date(System.currentTimeMillis()))
                .build();

        Clothes newClothes = clothesRepository.save(clothes);

        // 저장된 옷 정보 체크
        Optional<Clothes> clothesOptional = clothesRepository.findById(newClothes.getId());
        if (!clothesOptional.isPresent()) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_CLOTHES);
        }

        // 옷 정보 반환
        ClothesRegistResponseDto clothesRegistResponseDto = ClothesRegistResponseDto.builder()
                .clothes(clothesOptional.get())
                .build();

        return clothesRegistResponseDto;
    }

    /**
     * 옷 정보를 조회합니다.
     *
     * @param id
     * @return ClothesDetailResponseDto
     * @throws BaseException
     */
    @Override
    public ClothesDetailResponseDto detailClothes(Integer id, UUID memberId) throws BaseException {
        Optional<Member> memberOptional = memberRepository.findById(memberId);

        // 사용자 체크
        if (!memberOptional.isPresent()) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_MEMBER);
        }

        Optional<Clothes> clothesOptional = clothesRepository.findByIdAndMemberId(id, memberId);

        // 옷 정보 체크
        if (!clothesOptional.isPresent()) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_CLOTHES);
        }

        Clothes clothes = clothesOptional.get(); // 옷 정보

        // 옷 정보 반환
        ClothesDetailResponseDto clothesDetailResponseDto = ClothesDetailResponseDto.builder()
                .id(clothes.getId())
                .middleCategoryId(clothes.getMiddleCategory().getId())
                .largeCategoryId(clothes.getMiddleCategory().getLargeCategory().getId())
                .name(clothes.getName())
                .season(clothes.getSeason())
                .color(clothes.getColor())
                .thickness(clothes.getThickness())
                .price(clothes.getPrice())
                .shop(clothes.getShop())
                .textile(clothes.getTextile())
                .frequency(clothes.getFrequency())
                .star(clothes.getStar())
                .recentDate(clothes.getRecentDate())
                .build();

        return clothesDetailResponseDto;
    }

    /**
     * 옷 목록을 조회합니다.
     *
     * @return List<ClothesListResponseDto>
     * @throws BaseException
     */
    @Override
    public List<ClothesListResponseDto> listClothes(UUID memberId) throws BaseException {
        List<Clothes> clothesList = clothesRepository.findAllByMemberId(memberId); // 옷 목록 조회

        System.out.println(clothesList.size());

        // 옷 목록 정보 반환 (필요한 정보만 추출)
        List<ClothesListResponseDto> clothesListResponseDtoList = new ArrayList<>();
        for (Clothes clothes : clothesList) {
            ClothesListResponseDto clothesListResponseDto = ClothesListResponseDto.builder()
                    .id(clothes.getId())
                    .middleCategoryId(clothes.getMiddleCategory().getId())
                    .largeCategoryId(clothes.getMiddleCategory().getLargeCategory().getId())
                    .color(clothes.getColor())
                    .frequency(clothes.getFrequency())
                    .star(clothes.getStar())
                    .regDate(clothes.getRegDate())
                    .build();

            clothesListResponseDtoList.add(clothesListResponseDto);
        }

        return clothesListResponseDtoList;
    }

    /**
     * 옷 정보를 수정합니다.
     *
     * @param clothesUpdateRequestDto
     * @return ClothesUpdateResponseDto
     * @throws BaseException
     */
    @Override
    public ClothesUpdateResponseDto updateClothes(ClothesUpdateRequestDto clothesUpdateRequestDto, UUID memberId) throws BaseException {
        Integer id = clothesUpdateRequestDto.getId();

        // 옷 정보 체크
        Optional<Clothes> clothesOptional = clothesRepository.findById(id);
        if (!clothesOptional.isPresent()) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_CLOTHES);
        }

        // 중분류 카테고리 정보 체크
        Optional<MiddleCategory> middleCategoryOptional = middleCategoryRepository.findById(clothesUpdateRequestDto.getMiddleCategoryId());
        if (!middleCategoryOptional.isPresent()) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_MIDDLE_CATEGORY);
        }

        // 옷 정보 수정
        Clothes clothes = clothesOptional.get();
        Clothes newClothes = clothes.toBuilder()
                .middleCategory(middleCategoryOptional.get())
                .name(clothesUpdateRequestDto.getName())
                .season(clothesUpdateRequestDto.getSeason())
                .color(clothesUpdateRequestDto.getColor())
                .thickness(clothesUpdateRequestDto.getThickness())
                .price(clothesUpdateRequestDto.getPrice())
                .shop(clothesUpdateRequestDto.getShop())
                .textile(clothesUpdateRequestDto.getTextile())
                .build();

        clothesRepository.save(newClothes);

        // 수정된 옷 정보 반환
        Clothes updateClothes = clothesRepository.findById(id).get();
        ClothesUpdateResponseDto clothesUpdateResponseDto = ClothesUpdateResponseDto.builder()
                .clothes(updateClothes)
                .build();

        return clothesUpdateResponseDto;
    }

    /**
     * 옷 정보를 삭제합니다.
     *
     * @param id
     * @throws BaseException
     */
    @Override
    public void deleteClothes(Integer id, UUID memberId) throws BaseException {
        clothesRepository.deleteById(id); // 옷 정보 삭제
    }

    @Override
    public ClothesStarResponseDto starClothes(Integer id) throws BaseException {
        return null;
    }

    /**
     * 옷 즐겨찾기를 등록 / 해제합니다.
     *
     * @param id
     * @return ClothesStarResponseDto
     * @throws BaseException
     */
    @Override
    public ClothesStarResponseDto starClothes(Integer id, UUID memberId) throws BaseException {
        Optional<Clothes> clothesOptional = clothesRepository.findById(id);

        // 옷 정보 체크
        if (!clothesOptional.isPresent()) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_CLOTHES);
        }

        Clothes clothes = clothesOptional.get();

        // 옷 즐겨찾기 정보 수정 (등록 => 해제, 해제 => 등록)
        Clothes newClothes = clothes.toBuilder()
                .id(clothes.getId())
                .star(clothes.getStar() == 0 ? 1 : 0)
                .build();

        clothesRepository.save(newClothes);

        // 옷 즐겨찾기 정보 반환
        Clothes starClothes = clothesRepository.findById(id).get();
        ClothesStarResponseDto clothesStarResponseDto = ClothesStarResponseDto.builder()
                .id(starClothes.getId())
                .star(starClothes.getStar())
                .build();

        return clothesStarResponseDto;
    }


    /**
     * 카테고리별 가격 분석 서비스 입니다.
     * @param memberId
     * @return
     * @throws BaseException
     */
    @Override
    public ClothesAnalysisCostResponseDto analysisCost(UUID memberId) throws BaseException {

        ClothesAnalysisCostResponseDto responseDto;

        //내 옷장 총 가격
        Integer myTotalCost = clothesRepository.findPriceByMemberId(memberId);
        //모든 회원의 옷 가격 평균
        Integer avgOfMembers = clothesRepository.findAvgOfPrice();
        //카데고리별 정보 받아오기
        List<ClothesAnalysisCostResponseDto.AnalysisCostItem> itemList = clothesRepository.findCostOfMyClothesByCategory(memberId);
        if(itemList.size() == 0 ){
            throw new BaseException(BaseResponseStatus.NOT_FOUND_CATEGORY_ANALYSIS_INFO);
        }

        responseDto = ClothesAnalysisCostResponseDto.builder()
                .myAnalysisCost(itemList)
                .myTotalCost(myTotalCost)
                .userTotalAvgCost(avgOfMembers)
                .build();

        return responseDto;
    }

    /**
     * 옷장을 색상 기준으로 분석합니다.
     *
     * @param memberId
     * @return ClothesAnalysisColorResponseDto
     * @throws BaseException
     */
    @Override
    public ClothesAnalysisColorResponseDto analysisColor(UUID memberId) throws BaseException {
        // 내 옷장 분석
        List<IClothesAnalysisColor> clothesAnalysisColorMyList = clothesRepository.findByColorMember(memberId);
        if (clothesAnalysisColorMyList.size() == 0) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_COLOR_ANALYSIS_INFO);
        }

        // 모든 사용자 옷장 분석
        List<IClothesAnalysisColor> clothesAnalysisColorUserList = clothesRepository.findByColor();
        if (clothesAnalysisColorUserList.size() == 0) {
            throw new BaseException(BaseResponseStatus.NOT_FOUND_COLOR_ANALYSIS_INFO);
        }

        // 분석 정보 반환
        ClothesAnalysisColorResponseDto clothesAnalysisColorResponseDto = ClothesAnalysisColorResponseDto.builder()
                .myAnalysisColor(clothesAnalysisColorMyList)
                .userAnalysisColor(clothesAnalysisColorUserList)
                .build();

        return clothesAnalysisColorResponseDto;
    }

    /**
     * 옷장을 계절 기준으로 분석합니다.
     *
     * @param memberId
     * @return ClothesAnalysisSeasonResponseDto
     * @throws BaseException
     */
    @Override
    public ClothesAnalysisSeasonResponseDto analysisSeason(UUID memberId) throws BaseException {
        List<IClothesAnalysisSeason> springClothes = clothesRepository.findBySeasonMember("1", memberId); // 봄 옷 분석
        List<IClothesAnalysisSeason> summerClothes = clothesRepository.findBySeasonMember("2", memberId); // 여름 옷 분석
        List<IClothesAnalysisSeason> autumnClothes = clothesRepository.findBySeasonMember("3", memberId); // 가을 옷 분석
        List<IClothesAnalysisSeason> winterClothes = clothesRepository.findBySeasonMember("4", memberId); // 겨울 옷 분석

        // 계절 옷 분석 정보 반환
        ClothesAnalysisSeasonResponseDto clothesAnalysisSeasonResponseDto = ClothesAnalysisSeasonResponseDto.builder()
                .springClothes(springClothes)
                .summerClothes(summerClothes)
                .autumnClothes(autumnClothes)
                .winterClothes(winterClothes)
                .build();

        return clothesAnalysisSeasonResponseDto;
    }
}

