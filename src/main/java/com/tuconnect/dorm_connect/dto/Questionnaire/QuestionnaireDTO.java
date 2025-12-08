package com.tuconnect.dorm_connect.dto.Questionnaire;

public record QuestionnaireDTO(
         Boolean smokes,
         Boolean drinks,
         Boolean partyHome,
         Boolean stayAtHome,

         Integer cleanliness,
         Boolean sharesCleaning,
         String mbti,
         Integer age,
         String specialty,

         Boolean earlyRiser,
         Integer bedtime,

         Boolean studiesInRoom,
         Integer needsQuiet,

         Integer guestFrequency,
         Boolean prefersSocialRoommate,

         Boolean cooksInDorm,
         Integer foodSharing,

         Integer entertainmentFrequency,
         Boolean usesHeadphones,

         Integer personalSpaceImportance,
        Boolean sharesItems
) {
}
