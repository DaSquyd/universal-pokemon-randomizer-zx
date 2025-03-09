; r0    playerId
; r1    trainerId
; r2    partyPtr
; r3    heapId
; arg0  trainerDataPtr
; arg4  trainerPokePtr
; arg8  tempPokePtr
; argC  pokeDataSize
; arg10 pokeDataOffsets

; Similar to vanilla but randomization is seeded from the player's trainer ID

; TrTool_MakePokeFromData args
#define S_ArgHeapId 0x00
#define S_ArgDataOffsets 0x04 ; [0] = statModifiers, [1] = item, [2] = moves
#define S_ArgTeamRand 0x08
#define S_ArgTrainerDataPtr 0x0C
#define S_ArgTempPokePtr 0x10

#define STACK_SIZE 0x14

#define PUSH_SIZE (5 * 4) ; r4-r7, lr

#define ARG_OFFSET (STACK_SIZE + PUSH_SIZE)

#define ARG_TrainerDataPtr (ARG_OFFSET + 0x00)
#define ARG_TrainerPokePtr (ARG_OFFSET + 0x04)
#define ARG_TempPokePtr (ARG_OFFSET + 0x08)
#define ARG_PokeDataSize (ARG_OFFSET + 0x0C)
#define ARG_PokeDataOffsets (ARG_OFFSET + 0x10)

    push    {r4-r7, lr}
    sub     sp, #STACK_SIZE
    mov     r5, r0
    mov     r6, r1
    mov     r7, r2
    str     r3, [sp, #S_ArgHeapId]
    ldr     r0, [sp, #ARG_TrainerDataPtr]
    str     r0, [sp, #S_ArgTrainerDataPtr]
    ldr     r0, [sp, #ARG_TempPokePtr]
    str     r0, [sp, #S_ArgTempPokePtr]
    ldr     r0, [sp, #ARG_PokeDataOffsets]
    str     r0, [sp, #S_ArgDataOffsets]
    
    #printf("ARM9::TrTool_LoadParty_Standard (LR=0x%08X)", lr)
    
RandomSetup:
    #printf("    Poke size: %d", r7)
    
    ldr     r0, [sp, #S_ArgTrainerDataPtr]
    ldrb    r0, [r0, #TrainerData.class]
    bl      ARM9::TrTool_GetClassGender
    cmp     r0, #1 ; female
    bne     RandomSetup_Male
    
RandomSetup_Female:
    mov     r0, #120
    b       MainLoop_Setup

RandomSetup_Male:
    mov     r0, #136
    
RandomSetup_Finish:
    str     r0, [sp, #S_ArgTeamRand]
    
; iterate through each poke
MainLoop_Setup:
    mov     r4, #0
    
MainLoop_Start:
    #printf("    Poke %d...", r4)
    
    ldr     r0, [sp, #ARG_TrainerPokePtr]
    ldr     r1, [sp, #ARG_PokeDataSize]
    mul     r1, r4
    add     r0, r1 ; r0 := trainerPokePtr + (size * index)
    ldr     r1, [sp, #ARG_PokeDataSize]
    mov     r2, r5
    mov     r3, r6
    bl      ARM9::TrTool_MakePokeFromData
    
MainLoop_AddToParty:
    mov     r0, r7
    ldr     r1, [sp, #ARG_TempPokePtr]
    bl      ARM9::PokeParty_Add
    #printf("    added poke to party 0x%08X", r7)
    
MainLoop_End:
    add     r4, #1
    ldr     r0, [sp, #S_ArgTrainerDataPtr]
    ldrb    r0, [r0, #TrainerData.partySize]
    cmp     r4, r0
    bcc     MainLoop_Start
    
    add     sp, #STACK_SIZE
    pop     {r4-r7, pc}
    