; r0: playerId
; r1: trainerId
; r2: partyPtr
; r3: heapId
; arg0: trainerDataPtr
; arg4: trainerPokePtr
; arg8: pokePtr

; Similar to vanilla but randomization is seeded from the player's trainer ID

; Trainer_MakePoke args
#define S_HeapId 0x00
#define S_DataOffsets 0x04 ; [0] = statModifiers, [1] = item, [2] = moves
#define S_TeamRand 0x08
#define S_TrainerDataPtr 0x0C
#define S_ArgTempPokePtr 0x10

#define S_PlayerId 0x14
#define S_TrainerId 0x18
#define S_PartyPtr 0x1C
#define S_PokeSize 0x24
#define S_PokeIndex 0x2C
#define STACK_SIZE 0x34

#define PUSH_SIZE (5 * 4) ; r4-r7, lr

#define ARG_OFFSET (STACK_SIZE + PUSH_SIZE)

#define ARG_TrainerDataPtr (ARG_OFFSET + 0x00)
#define ARG_TrainerPokePtr (ARG_OFFSET + 0x04)
#define ARG_TempPokePtr (ARG_OFFSET + 0x08)

    push    {r4-r7, lr}
    sub     sp, #STACK_SIZE
    str     r0, [sp, #S_PlayerId]
    str     r1, [sp, #S_TrainerId]
    str     r2, [sp, #S_PartyPtr]
    str     r3, [sp, #S_HeapId]
    ldr     r0, [sp, #ARG_TrainerDataPtr]
    str     r0, [sp, #S_TrainerDataPtr]
    ldr     r0, [sp, #ARG_TempPokePtr]
    str     r0, [sp, #S_ArgTempPokePtr]
    
    #printf("ARM9::TrTool_LoadParty_Standard (LR=0x%08X)", lr)
    
    mov     r7, #TrainerPoke.BASE_SIZE
    
    mov     r0, #0
    str     r0, [sp, #S_DataOffsets]
    
CheckPokesHaveStatModifiers:
    ldr     r0, [sp, #S_TrainerDataPtr]
    bl      ARM9::TrTool_PokesHaveStatModifiers
    cmp     r0, #FALSE
    beq     CheckPokesHaveItems
    
    #printf("    Pokes have stat modifiers flag")
    
    add     r0, sp, #S_DataOffsets
    strb    r7, [r0, #0x00]
    add     r7, #TrainerPoke.STAT_MODIFIERS_SIZE
    
CheckPokesHaveItems:
    ldr     r0, [sp, #S_TrainerDataPtr]
    bl      ARM9::TrTool_PokesHaveItems
    cmp     r0, #FALSE
    beq     CheckPokesHaveMoves
    
    #printf("    Pokes have items flag")
    
    add     r0, sp, #S_DataOffsets
    strb    r7, [r0, #0x01]
    add     r7, #TrainerPoke.ITEM_SIZE
    
CheckPokesHaveMoves:
    ldr     r0, [sp, #S_TrainerDataPtr]
    bl      ARM9::TrTool_PokesHaveMoves
    cmp     r0, #FALSE
    beq     RandomSetup
    
    #printf("    Pokes have moves flag")
    
    add     r0, sp, #S_DataOffsets
    strb    r7, [r0, #0x02]
    add     r7, #TrainerPoke.MOVES_SIZE
    
RandomSetup:
    #printf("    Poke size: %d", r7)
    
    ldr     r0, [sp, #S_TrainerDataPtr]
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
    str     r0, [sp, #S_TeamRand]
    
; iterate through each poke
MainLoop_Setup:
    mov     r0, #0
    str     r0, [sp, #S_PokeIndex]
    
MainLoop_Start:
    #printf("    Poke %d...", ldr [sp, #S_PokeIndex])
    
    ldr     r0, [sp, #S_PokeIndex]
    mul     r0, r7 ; r0 := size * index
    
    ldr     r1, [sp, #ARG_TrainerPokePtr]
    add     r6, r1, r0
    
    mov     r0, r6
    mov     r1, r7
    ldr     r2, [sp, #S_PlayerId]
    ldr     r3, [sp, #S_TrainerId]
    bl      ARM9::TrTool_MakePokeFromData
    
MainLoop_AddToParty:
    ldr     r0, [sp, #S_PartyPtr]
    ldr     r1, [sp, #ARG_TempPokePtr]
    bl      ARM9::PokeParty_Add
    #printf("    added poke to party 0x%08X", ldr [sp, #S_PartyPtr])
    
MainLoop_End:
    ldr     r0, [sp, #S_PokeIndex]
    add     r0, #1
    str     r0, [sp, #S_PokeIndex]
    ldr     r1, [sp, #S_TrainerDataPtr]
    ldrb    r1, [r1, #TrainerData.partySize]
    cmp     r0, r1
    bcc     MainLoop_Start
    
    add     sp, #STACK_SIZE
    pop     {r4-r7, pc}
    