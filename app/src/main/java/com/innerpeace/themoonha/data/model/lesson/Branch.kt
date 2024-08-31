package com.innerpeace.themoonha.data.model.lesson

enum class Branch(val branchId: Long, val branchName: String) {
    TRADE_CENTER(1, "무역센터점"),
    CHEONHO(2, "천호점"),
    SINCHON(3, "신촌점"),
    MIA(4, "미아점"),
    MOKDONG(5, "목동점"),
    KINTEX(6, "킨텍스점"),
    DCUBE_CITY(7, "디큐브시티"),
    PANGYO(8, "판교점"),
    HYUNDAI_SEOUL(9, "더현대 서울"),
    JUNG_DONG(10, "중동점"),
    APGUJEONG(11, "압구정본점"),
    HYUNDAI_DAEGU(12, "더현대 대구");

    companion object {
        fun getBranchById(branchId: Long): Branch? {
            return values().find { it.branchId == branchId }
        }
    }
}