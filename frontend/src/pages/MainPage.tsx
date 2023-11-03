import React, { useEffect, useState } from 'react';
// import { useNavigate } from 'react-router-dom';

import MainInfo from '../components/main/organisms/MainInfo';
import PickButtonTap from '../components/main/organisms/PickButtonTap';
import RecommendList from '../components/main/organisms/RecommendList';
import MapModal from '../components/main/organisms/MapModal';
import { authInstance } from '../api/api';

// 날씨 api 사용
// import Weather from "../api/Weather";

const MainPage = () => {
  //   const navigate = useNavigate();
  // 현재 위치
  const [currentLocation, setCurrentLocation] = useState<{
    latitude: number;
    longitude: number;
  } | null>(null);

  // 법정동 주소
  const [address, setAddress] = useState<string>(() => {
    return window.localStorage.getItem('address') || '';
  });

  // 주소 로컬 스토리지에 저장하기
  useEffect(() => {
    window.localStorage.setItem('address', address);
  }, [address]);

  // 지도 출력
  const [locationState, setLocationState] = useState<boolean>(false);

  // 지도 화면 출력 클릭 이벤트
  const showLocationClick = () => {
    setLocationState(!locationState);
  };

  // 지도 다시 불러오기
  const [resetLocation, setResetLocation] = useState<boolean>(false);

  // 주소 검색 이벤트 핸들러
  const [newLocation, setNewLocation] = useState<string>('');

  const handleInputChange = (newValue: any) => {
    setNewLocation(newValue);
  };

  // 옷 추천 리스트 가상 데이터
  const RecommendClothesListData: any = {
    recommenClothesInfo: [
      // 추천 옷 목록
      {
        clothesInAIOutfit: [
          // 날짜별 AI 추천 옷 정보 리스트
          {
            clothesId: 21, // 옷 id
            imageUrl: '/images/clothes1.png', // 옷 이미지
          },
          {
            clothesId: 22, // 옷 id
            imageUrl: '/images/clothes2.png', // 옷 이미지
          },
          {
            clothesId: 23, // 옷 id
            imageUrl: '/images/clothes3.png', // 옷 이미지
          },
          {
            clothesId: 24, // 옷 id
            imageUrl: '/images/clothes4.png', // 옷 이미지
          },
        ],
      },
      {
        clothesInAIOutfit: [
          // 날짜별 AI 추천 옷 정보 리스트
          {
            clothesId: 1, // 옷 id
            imageUrl: '/images/clothes1.png', // 옷 이미지
          },
          {
            clothesId: 1, // 옷 id
            imageUrl: '/images/clothes2.png', // 옷 이미지
          },
          {
            clothesId: 1, // 옷 id
            imageUrl: '/images/clothes3.png', // 옷 이미지
          },
          {
            clothesId: 1, // 옷 id
            imageUrl: '/images/clothes4.png', // 옷 이미지
          },
        ],
      },
      {
        clothesInAIOutfit: [
          // 날짜별 AI 추천 옷 정보 리스트
          {
            clothesId: 1, // 옷 id
            imageUrl: '/images/clothes1.png', // 옷 이미지
          },
          {
            clothesId: 1, // 옷 id
            imageUrl: '/images/clothes2.png', // 옷 이미지
          },
          {
            clothesId: 1, // 옷 id
            imageUrl: '/images/clothes3.png', // 옷 이미지
          },
          {
            clothesId: 1, // 옷 id
            imageUrl: '/images/clothes4.png', // 옷 이미지
          },
        ],
      },
    ],

    recommenWeatherInfo: [
      // 추천 날씨 목록
      {
        minTemperature: 10, // 최저 기온
        maxTemperature: 20, // 최고 기온
        weather: 1, // 날씨 정보 (맑음, 구름 조금 등)
      },
      {
        minTemperature: 15, // 최저 기온
        maxTemperature: 25, // 최고 기온
        weather: 1, // 날씨 정보 (맑음, 구름 조금 등)
      },
      {
        minTemperature: 5, // 최저 기온
        maxTemperature: 10, // 최고 기온
        weather: 1, // 날씨 정보 (맑음, 구름 조금 등)
      },
    ],
  };

  // interface ClothesRecommendation {
  //     clothesInfo: {
  //         clothesId: number;
  //         largeCategoryId: string;
  //         imageUrl: string;
  //     }[];
  //     recDate: Date;
  // }

  // 옷 추천 리스트
  const [clothesListData, setClothesListData] = useState<any>([]);
  // const navigate = useNavigate();
  const clothesData = async () => {
    const requesBody = [
      {
        // 날씨 정보
        sky: 0, // 하늘 상태
        pty: 0, // 강수 형태
        tmn: 0, // 일 최저 기온
        tmx: 0, // 일 최고 기온
        wsd: 0, // 풍속
        date: '2023-11-02', // 날짜 - "2023-11-02"
      },
      {
        // 날씨 정보
        sky: 0, // 하늘 상태
        pty: 0, // 강수 형태
        tmn: 0, // 일 최저 기온
        tmx: 0, // 일 최고 기온
        wsd: 0, // 풍속
        date: '2023-11-03', // 날짜 - "2023-11-02"
      },
      {
        // 날씨 정보
        sky: 0, // 하늘 상태
        pty: 0, // 강수 형태
        tmn: 0, // 일 최저 기온
        tmx: 0, // 일 최고 기온
        wsd: 0, // 풍속
        date: '2023-11-04', // 날짜 - "2023-11-02"
      },
    ];

    try {
      // 토큰이 필요한 api의 경우 authInstance를 가져옵니다
      const axiosInstance = authInstance({ ContentType: 'application/json' });
      const response = await axiosInstance.post('/ai-rec-outfits/combine', requesBody);

      console.log('추천 착장 조회 성공', response.data.data);
      setClothesListData(response.data.data);

      return response.data;
    } catch (error) {
      console.log('옷 목록 데이터 조회 실패', error);

      // if (error.response.data.status === 500) {
      //     navigate('/mycloset/add-cloth')
      //     // alert('보유한 옷이 적어 추천이 불가능합니다. 옷을 등록해주세요.')
      // }

      // 보유한 옷이 적어 추천이 불가능합니다.

      // throw new Error('옷 목록 데이터 조회 실패');
      return null;
    }
  };

  // 추천 리스트 하루에 한 번만 불러오기
  // const ONE_HOUR = 60 * 60 * 1000;

  useEffect(() => {
    // const lastFetched = Number(localStorage.getItem('lastFetched'));
    // const currentTime = new Date().getTime();

    // if (!lastFetched || currentTime - lastFetched > ONE_HOUR) {
    //     // If lastFetched is not set or more than a day has passed, fetch data
    //     clothesData();

    //     // Update the lastFetched time in localStorage
    //     localStorage.setItem('lastFetched', currentTime.toString());
    // }
    clothesData();
  }, []);

  // 날씨 기반 리스트
  const [weatherListData, setWeatherListData] = useState<any>([]);

  useEffect(() => {
    setWeatherListData(RecommendClothesListData.recommenWeatherInfo);
  }, []);

  // clothesListData.length > 0 ? (
  //     <div>
  //         <br />
  //         {/* 날씨 기반 추천 리스트 */}
  //         <RecommendList clothesListData={clothesListData} weatherListData={weatherListData} />
  //     </div>
  // ) : (
  //     <div className="border border-pink-hot w-1/4 h-[50px] flex items-center justify-center rounded-2xl font-WebBody1 font-bold text-pink" onClick={() => navigate('/mycloset/add-cloth')}>옷 등록하러 가기</div>
  // )}

  return (
    <>
      <div className="flex flex-col p-4">
        <MainInfo currentLocation={currentLocation} address={address} showLocationClick={showLocationClick} />
        <div>
          <br />
          {/* 날씨 기반 추천 리스트 */}
          <RecommendList clothesListData={clothesListData} weatherListData={weatherListData} />
        </div>

        <br />
        {/* 버튼 탭 */}
        <PickButtonTap />
        {/* 지도  */}
        {locationState && (
          <div className="absolute z-50 top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 h-[60vh] w-[50vw] max-w-[400px] min-w-[300px]">
            <MapModal
              currentLocation={currentLocation}
              address={address}
              locationState={locationState}
              setCurrentLocation={setCurrentLocation}
              setAddress={setAddress}
              resetLocation={resetLocation}
              setResetLocation={setResetLocation}
              showLocationClick={showLocationClick}
              handleInputChange={handleInputChange}
              newLocation={newLocation}
            />
          </div>
        )}
      </div>
    </>
  );
};

export default MainPage;
