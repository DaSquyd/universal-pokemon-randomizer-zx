; r0    *battleSetup
; r1    *gameData
; r2    clientId
; r3    **party
; arg0  trainerId
; arg4  heapId

#define S_GameData 0x00
#define STACK_SIZE 0x04

#define PUSH_SIZE (6 * 4) ; r3-r7, lr

#define ARG_OFFSET (STACK_SIZE + PUSH_SIZE)

#define ARG_TrainerId (ARG_OFFSET + 0x00)
#define ARG_HeapId (ARG_OFFSET + 0x04)

    push    {r3-r7, lr}
    sub     sp, #STACK_SIZE
    mov     r4, r0 ; r4 := *battleSetup
    mov     r5, r3 ; r5 := **party
    mov     r6, r2 ; r6 := clientId
    str     r1, [sp, #S_GameData]
    
    #printf("ARM9::BattleSetup_LoadTrainer")
    #printf("    trainerId=%d", ldr [sp, #ARG_TrainerId])
    #printf("    heapId=%d", ldr [sp, #ARG_HeapId])

CheckParty:
    ldr     r0, [r5] ; r0 := *party
    cmp     r0, #0 ; nullptr
    bne     CheckTrainerData
    
CreateParty:
    ldr     r0, [sp, #ARG_HeapId]
    bl      ARM9::PokeParty_Create
    str     r0, [r5]
    #printf("    created poke party at 0x%08X", r0)
    
    
CheckTrainerData:
    mov     r7, r4 ; r7 := *battleSetup
    lsl     r6, #2
    add     r7, #0x48 ; battleSetup->trainerSetups TODO
    ldr     r0, [r7, r6]
    cmp     r0, #0
    bne     CheckTrainerId
    
SetTrainerData:
    ldr     r0, [sp, #ARG_HeapId]
    bl      ARM9::BattleSetup_CreateTrainerData
    str     r0, [r7, r6]
    #printf("    created trainer data at 0x%08X", r0)
    
    
CheckTrainerId:
    ldr     r1, [sp, #ARG_TrainerId]
    cmp     r1, #0
    beq     Return
    
    ldr     r0, [sp, #S_GameData] ; gameData
    ldr     r2, [r7, r6] ; setupParam
    ldr     r3, [sp, #ARG_HeapId] ; heapId
    bl      ARM9::TrTool_LoadTrainer
    #printf("    loaded trainer")
    
    ldr     r0, [sp, #S_GameData] ; gameData (added this arg)
    ldr     r1, [sp, #ARG_TrainerId] ; trainerId
    ldr     r2, [r5] ; party
    ldr     r3, [sp, #ARG_HeapId] ; heapId
    bl      ARM9::TrTool_LoadParty_Core ; new
    
#if DEBUG
    ldr     r0, [sp, #S_GameData]
    bl      ARM9::GameData_GetSeason
    #printf("    season=%d", r0)
#endif
    
    ldr     r0, [sp, #S_GameData]
    bl      ARM9::GameData_GetSeason
    mov     r2, r0
    
    ldr     r0, [sp, #S_GameData]
    ldr     r1, [r5]
    bl      Local::TransformSeasonPoke
    ldr     r0,  [r7, r6]
    ldr     r0,  [r0, #0x04] ; classId
    bl      ARM9::TrTool_GetClassGroup
    cmp     r0, #0x0E
    bhi     SetBGM_Win2
    
    ; TODO: is this different in BW?
    #switch r0
    #case SetBGM_Win3 ; gym leader
    #case SetBGM_Win3 ; elite four
    #case SetBGM_Win2 ; cynthia
    #case SetBGM_Win2 ; rival
    #case SetBGM_Win2 ; pkmn trainer colress
    #case SetBGM_Win4 ; team plasma colress
    #case SetBGM_Win2 ; N
    #case SetBGM_Win6 ; team plasma
    #case SetBGM_Win5 ; champion
    #case SetBGM_Win4 ; ghetsis
    #case SetBGM_Win6 ; zinzolin
    #case SetBGM_Win2 ; black tower/white tree hollow boss
    #case SetBGM_Win2 ; postgame colress
    #case SetBGM_Win2 ; rival 2
    #case SetBGM_Win5 ; subway boss
    
SetBGM_Win3:
    ldr     r0, =1150
    strh    r0, [r4, #0x1A]
    b       Return
    
SetBGM_Win4:
    ldr     r0, =1151
    strh    r0, [r4, #0x1A]
    b       Return
    
SetBGM_Win5:
    mov     r0, #(1152 >> 4)
    lsl     r0, #4
    strh    r0, [r4, #0x1A]
    b       Return
    
SetBGM_Win6:
    ldr     r0, =1165
    strh    r0, [r4, #0x1A]
    b       Return
    
SetBGM_Win2:
    ldr     r0, =1149
    strh    r0, [r4, #0x1A]
    
Return:
    add     sp, #STACK_SIZE
    pop     {r3-r7, pc}