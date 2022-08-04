# [tutti] 음악 스트리밍 및 공유 서비스
![Frame 149 (1)](https://user-images.githubusercontent.com/74285387/182149207-bd2894eb-00bb-41af-ac7c-30bae7cb1ed7.jpg)
### 음악 공유 및 온라인 비대면 라이브 공연 서비스


## [함께 하는 음악 생활, tutti로 가기](https://tuttimusic.shop)


<a href="https://www.notion.so/tuttimusic/6-bb79c160d1a248c69faae0e247053204" target="_blank">**Project tutti Notion SA Home**</a>



[**Front End Github**](https://github.com/itsstacy/tuttimusicFE)

[**Back End Github**](https://github.com/doyupK/tutti_be)


## 📚 서비스 아키텍쳐
![tutti architecture_220729 drawio](https://user-images.githubusercontent.com/74285387/182148463-3121ba23-7841-46bf-a9e3-a82ea953507e.png)

<details>
<summary>
Front & Back
</summary>

- Stomp, Sock, Redis
    - 소켓 통신 기반으로 실시간 채팅 구현
    - stomp의 경우 java에 종속적이어서 react-spring환경에서 많이 사용
    - [socket.io](http://socket.io) 보다 가볍고 subscribe 방식이라 더 안정적으로 구동
    - Redis로 캐싱 기능 역할을 써서 DB Connection을 최소화
- OpenVidu
    - Kurento 기반의 중개 서버를 에플리케이션에 추가
    - 웹소켓이 아닌 브라우저끼리 연결시키는 WebRTC 방법을 이용해 데이터 스트림이 가능
- SSE
    - tutti의 실시간 알림은 단방향 구조로 충분하기에 상대적으로 무거운 Websocket 보다는 SSE로 적용
- Github Action
    - CI/CD용 툴
    - 깃허브에서 코드를 같이 관리하면서 배포시점 까지 설정할 수 있어서 편하다.
    - 깃허브에서 develop 브랜치를 default로 설정하고, 실제로 배포되는 브랜치는 master로 설정
    - **Front**: AWS 클라우드프론트 - create invalidation 까지 연결해서 캐싱을 삭제함 → 재배포 즉시 웹에 업데이트 반영
</details>

<details>
<summary>
Front-End
</summary>

- redux
    - mobx와 recoil이 리덕스보다 사용법이 간단하지만 레퍼런스는 redux가 더 많고 점유율 또한 더 높아서 기본을 탄탄하게 잡아가기 위해 선택
    - **redux toolkit**: redux-thunk, immer 등 추가 패키지들이 다 포함되어 있어 따로 설치하지 않아도 되고 redux의 boillerplate를 짧게 줄일 수 있다.
- Cloudfront
    - AWS에서 S3, certificate manager, route53, Cloudfront 를 one-stop으로 이용 가능
    - AWS S3 버킷과 연동이 쉽고 git actions 설정 시 S3와 같은 context로 연결해주기 때문에 설정이 간편하다.
</details>

<details>
<summary>
Back-End
</summary>

![tutti_Back_End_architecture_220730](https://user-images.githubusercontent.com/103116643/182825087-08c3b472-aa6b-42d4-bc61-f4b4f07621f2.png)

- Prometheus + Grafana
    - 서버의 상태를 실시간으로 모니터링해서 문제 발생 시 유지 보수 목적으로 적용
    - 시각화가 되어있어 에러 발생 시 언제, 어디에서 발생했는지 쉽게 구별이 가능
    - 최대, 최소 트래픽이 쉽게 파악이 가능해서 하루 중 트래픽이 언제 많이 발생하는지 파악이 쉽다.
- Nginx
    - 추후 대량 트래픽이 발생 했을 시, 로드 밸런싱을 구성하기에 용이함
    - SSL 암호화로 서버의 부담을 줄여줌
- Docker
    - 재 배포가 필요할 때, 기존 컨테이너의 레이어와 배포되는 레이어가 같으면 재사용할 수 있어서 프로세스가 훨씬 빨라짐
    - 별도의 OS 사용이 필요하지 않게 되어, 성능적인 개선, 효율적인 메모리 사용
</details> 
<br>

## 📚 서비스 ERD

![image](https://user-images.githubusercontent.com/103116643/182820672-f9e243e0-f0e6-4b5d-b3e1-761e2dd8d254.png)

<br>

## 📚 실시간 모니터링 Grafana
![image](https://user-images.githubusercontent.com/103116643/182821904-0c39654a-0b74-47fc-a3f0-8baccb3606e2.png)



<br>

## 🎵 tutti의 핵심 서비스 기능

<details>
<summary>
<b>음악 플레이 기능 🎵</b>
</summary>
<br>
tutti에는 다양한 음악들이 있습니다.<br>
음악을 듣고 싶다면 음악을 클릭해 해당 음악을 들어보세요!<br>
음악을 플레이 했다면, 페이지를 벗어나도 🎵<b>하단 음악 플레이어</b> 를 통해 끊김 없이 음악을 들을 수 있어요!<br>
</details>

<details>
<summary>
<b>장르별 음악 검색 🔍</b>
</summary>
<br>
FEED 페이지에서 원하는 장르를 선택해 해당 장르의 음악을 찾아볼 수 있어요!<br>
또한 검색창을 통해서 🔍 <b>아티스트와 음악을 검색 </b>할 수 있으며 <br>
곡 또는 아티스트를 선택하면 해당 페이지로 넘어갈 수 있어요!   <br>
</details>

<details>
<summary>
<b>아티스트 페이지 (&실시간 알림) 💌</b>
</summary>
<br>
음악을 듣다 마음에 드는 아티스트를 발견했다면? <br>
😎<b>아티스트의 프로필 페이지</b> 를 방문해 업로드한 음악이나 SNS(유튜브, 인스타그램)등<br>
해당 아티스트에 대해 더 알 수 있어요!<br>
또한 아티스트를 ❤<b>팔로잉</b>을 하면 아티스트가 라이브를 시작할 때,<br>
제일 먼저 <b>💌알림</b>을 받을 수도 있어요!<br>
</details>

<details>
<summary>
<b> 음악 라이브 방송 및 실시간 채팅 기능 🎙</b>
</summary>
<br>
나의 노래나 연주를 사람들에게 들려주고 바로 반응을 보고 싶다면?<br>
🎙 <b>라이브 방송과 실시간 채팅 기능 </b>을 통해서 다른 사용자들과 함께 소통도 가능해요<br>
다른 사용자들의 반응이 필요하다면 라이브 기능을 사용해 보세요!<br>
</details>
<br>

## ⚒ 트러블 슈팅

### 💃 Front-End

#### 라이브 페이지에서 뒤로가기를 누르면 이전 컴포넌트에서 useEffect 내에 navigate가 작동되어 다시 라이브 페이지로 돌아오는 현상
<details>
<summary>
4-step 해결과정
</summary>
    
- <b>문제 발생</b>
    - 라이브 방 만들기 후 나오는 로딩 컴포넌트를 거쳐 라이브 방으로 들어가는데, 이때 사용자가 뒤로가기를 했을 때 로딩 컴포넌트로 돌아간다. 돌아가자마자 useEffect 내에 navigate가 작동되어 다시 방으로 돌아온다.
- <b>선택지</b>
    - 1안: 뒤로가기 action 자체를 막기
    - 2안: prompt를 사용해서 페이지 이탈 시 특정 location으로 이동시키면 사용자가 원하는 페이지로 이동하지 않는다고 생각할 수 있기 때문에 올바른 해결법이 아님

        ex) navbar에 “feed”를 눌러서 페이지를 이탈했는데 홈(정해진 location)으로 이동되면 좋지 않은 UX이다.

    - 3안: 리덕스를 이용해 한번 로딩 페이지를 거쳤으면 전역 상태를 바꿔서 다시 입장하게 되면 navigate 이 작동하지 않도록 한다.
    - 4안: 뒤로가기 했을 때 세션이 종료됐으면 다시 라이브 방으로 안 들어가고 다른 곳으로  navigate시키기
- <b>의견 결정</b>
    - 3안: 리덕스를 사용해 로딩 페이지를 거쳤으면 전역 상태를 바꿔서 다시 입장하게 되면 navigate 이 작동하지 않도록 한다.
        - checkSession이라는 reducer를 생성
        - 방 만들 때 videoSession: false (기본값 false)
        - 로딩 페이지를 거쳐 라이브방에 입장하면 videoSession: true 로 상태변경
        - 뒤로가기 했을 때, videoSession: true이면 라이브 방이 아니라 채팅방 리스트로 이동하도록 설정
        - 다시 방을 만들 때 videoSession: false로 상태변경
- <b>배운점</b>
    - 페이지 이동 시 상태를 저장하고 싶을 때 전역상태관리를 사용할 수 있다.
    - useEffect 사용법과 function 배치에 대한 이해
</details>

- 코드로 보는 해결 과정
    <details>
    <summary>
     수정 전
    </summary>
            ```jsx
        // LoadingLive.js
        useEffect(() => {
              setTimeout(()=> {
              navigate(`/live/${userName}`);
            },2000)
            }
          },[])
        ```
    </details>

    <details>
    <summary>    
    수정 후
    </summary>

        ```jsx
        // LoadingLive.js
        const videoInfo = useSelector((state)=> state.Video.video);
          const session = videoInfo.videoSession
        useEffect(() => {
            if (session===true) {
              navigate('/livelist')
              dispatch(checkSession(false))
            } else {
              setTimeout(()=> {
              navigate(`/live/${userName}`);
            },2000)
            }
          },[])
        ```
      </details>  

  <details>
  <summary>         
  로딩 페이지 (screenshot)
  </summary>  
    <img src="https://user-images.githubusercontent.com/74285387/182149619-f27e609c-4ae3-461a-a81c-9f228178849a.png"/>
</details>  

#### Sync로 작동하는 두 개의 플레이어 중 하나를 껐을 때 다른 쪽의 플레이어를 조작하면 흰 화면이 뜨는 현상
<details>
<summary>
4-step 해결과정
</summary>

- <b>문제 발생</b>
    - detail페이지의 플레이어와 하단 플레이어는 sync로 작동 (play/pause, 볼륨조절, 원하는 구간으로 이동하는 기능)
    - sync로 작동하는 두 개의 플레이어 중 하나를 껐을 때 (하단 플레이어를 끄는 기능이 있음) 다른 쪽의 플레이어를 조작하면 흰 화면이 뜨는 현상
    - 한 쪽에서 플레이어를 조작하면 그로 인해 전역 상태가 변경되고 변경된 데이터가 다른 쪽 플레이어에 영향을 미쳐 sync로 작동됨
- <b>선택지</b>
    - 1안: 하단 플레이어 삭제
    - 2안: useSelector를 통해 가져오는 값이 있을 때만 해당 function이 작동되도록 수정
    - 3안: sync 기능을 없애고 독자적으로 조작하도록 수정
- <b>의견 결정</b>
    - 2안: useSelector를 통해 가져오는 값이 있을 때만 해당 function이 작동되도록 수정
- <b>배운점</b>
    - 구독하는 state 정보가 변경되었을 때, useSelector를 통해 가져오는 값이 없으면 해당 값을 이용한 function에서 에러가 발생할 수 있는 점 (function을 포함하는 component가 mount되어 있지 않더라도 작동)
    - 리덕스에 state 정보가 변경되면  component가 mount되어 있지 않더라도 구독하는 모든 component가 업데이트된 상태를 다시 받아오고 해당 값과 연결된 모든 function이 재 작동됨
</details>
    
- 코드로 보는 해결 과정

    <details>
    <summary>
    수정 전
    </summary>

        ```jsx
        // 하단 플레이어
        useEffect(()=>{
            setPlaying(_playing);
            if (allStop===false) {
              wavesurfer.current?.playPause();
            }
          },[_playing])
          useEffect(()=>{
            if (_volume) {
            setVolume(_volume);
            wavesurfer.current?.setVolume(_volume);
            }
          },[_volume])
          useEffect(()=>{
            setCurrentTime(_time);
            if (_time>0.2) {
              wavesurfer.current?.play(_time)
            }
          },[_time])
        ```
    </details>
    <details>
    <summary>   
    수정 후
    </summary>

        ```jsx
        // 하단 플레이어
        useEffect(()=>{
            setPlaying(_playing);
            if (_display===true&&allStop===false) {
              wavesurfer.current?.playPause();
            }
          },[_playing])
          useEffect(()=>{
            if (_display===true&&_volume) {
            setVolume(_volume);
            wavesurfer.current?.setVolume(_volume);
            }
          },[_volume])
          useEffect(()=>{
            setCurrentTime(_time);
            if (_display===true&&_time>0.2) {
              wavesurfer.current?.play(_time)
            }
          },[_time])
        ```
     </details>   
  <details>
  <summary>         
  에러 메세지 (console)
  </summary>  
    <img src="https://user-images.githubusercontent.com/74285387/182156329-94c5f916-955d-451d-a4ab-c5f48c1ae836.png"/>
  </details>
  <details>
  <summary>         
  detail 플레이어와 하단 플레이어 (screenshot)
  </summary>  
    <img src="https://user-images.githubusercontent.com/74285387/182149571-37f0f60a-7dd3-4d4d-b9ac-d8c273561a63.png"/>
  </details>

<br>       

### 💃 Back-End

### N+1
<details>
<summary>
4-step 해결과정
</summary>
    
- <b>도입 이유</b>

    통신간에 트래픽이 너무 크고, 느려서 연관 관계가 되어있는 엔티티 간에 필요한 데이터들을 추려서 가져와야하는 상황 발생

- <b>문제 상황</b>

    JPA 레포지토리를 통해 엔티티 객체를 조회할 때 그 객체와 연관되어 있는 객체의 데이터가 전부 같이나와 필요 없는 데이터까지 추출되어 트래픽 과부하와 불필요한 정제 코드들을 유발하는 문제

- <b>문제 해결 과정</b>
    1. JPA로 읽어와서 반복문을 통해 필요한 Dto를 생성하여 정제하자
    2. queryDsl을 적용시켜 innerJoin, leftJoin을 이용하여 간단, 정확하게 데이터를 가져오자
- <b>결론</b>

    (1) 방법으로 구현하면 트래픽은 줄어들지만, 서버 시간 복잡도가 증가하고, 쿼리문 자체가 많이 나가기 때문에 (2) 를 사용하여 조회 쿼리 자체에 필터링을 해서 최적화를 진행

    - 수정 이력

        수정 전

        ![Jpa 메인페이지 get 방식 액티브 스레드 오버 타임](https://user-images.githubusercontent.com/103116643/182169538-032c18f7-ce39-4c69-b329-3e49bc6a3407.png)

        VU :2000  / 1S

        = 1분 12초

        수정 후

        ![QueyDsl 메인페이지 get방식 액티브 스레드 오버 타임](https://user-images.githubusercontent.com/103116643/182169615-1291777f-9790-4850-946f-d8c75e8a15f2.png)

        VU : 2000 / 1S

        = 15초
</details>
    
### Redis 적용 시 컨테이너 간 통신 에러

<details>
<summary>
4-step 해결과정
</summary>
    
- <b>도입 이유</b>

    실시간 채팅 내역을 DB에 저장 하는데 DB Connection을 최소화하기 위해 버퍼로 사용하고, 실시간 채팅 캐싱 기능 활용하기 위해 도입 결정

- <b>문제 상황</b>

    스프링 부트 서버와 레디스 서버를 도커 컨테이너에 올렸으나, 컨테이너 간 통신 불능

- <b>문제 원인</b>

    도커 내부 네트워크를 인지하지 못한 상태로 컨테이너 간 End point를 localhost로 설정한 것이 문제였다.

    - Network Group

        ![화면 캡처 2022-07-30 151523](https://user-images.githubusercontent.com/103116643/182169687-692e9657-14c5-4e28-9c04-7721b1fdd0ce.png)


- <b>해결 과정</b>

    도커 내부 네트워크는 localhost가 아닌 별도의 내부 네트워크를 가지고 있어, 

    도커 네트워크 브릿지로 통신하여 성공
</details>
<br>

## ✍🏻 6조 동료에게..


>
>* to 아뱅지은*
>-
>-
>-
>-
>-
>-
>-


>
>* to 아닌가연*
>-
>-
>-
>-
>-
>-
>-


>
>* to 부들현명*
>-
>-
>-
>-
>-
>-
>-


>
>* to 보라정윤*
>-
>-
>-
>-
>-
>-
>-

>
> * to 완볍도엽*
>-
>-
>-
>-
>-
>-
>-

>
> * to 와장창창규*
>-
>-
>-
>-
>-
>-
>-

>
>* to 열세열*
>-
>-
>-
>-
>-
>-
>-

>
>* to 멍뭉민지*
>-
>-
>-
>-
>-
>-
>-



## 👋 tutti 팀원 정보

이름 | 포지션 | E-mail | Github 
---- | ---- | ---- | ----
권지은 (리더) | 	Front-End (React) | stacykwon86@gmail.com | https://github.com/itsstacy
김현명	| Front-End (React) | ftm513@gmail.com | https://github.com/hyunmyeong
이가연	| Front-End (React) | 2022gygy@gmail.com| https://github.com/gygy2022
이정윤	| Designer | jungyunleeee@gmail.com | 
김도엽 (리더) | Back-End (Spring) | gltlvl12@gmail.com | https://github.com/doyupK
김민지	| Back-End (Spring) | alswlwkd20@naver.com | https://github.com/minji-kim525
김창규	| Back-End (Spring) | kimchangkyu99@gmail.com | https://github.com/ck-kor
박세열	| Back-End (Spring) | dmot@naver.com | https://github.com/WE-DA-pluggg
