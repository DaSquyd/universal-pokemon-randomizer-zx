; r0    pokeDataPtr
; r1    pokeDataSize
; r2    playerId
; r3    trainerId
; arg0  heapId
; arg4  dataOffsets
; arg8  teamRand
; argC  trainerDataPtr
; arg10 tempPokePtr

; TrTool_GetRandomPersonal args
#define S_ArgHeapId 0x00

; PokeTool_SetupEx args
#define S_ArgIdHigh 0x00
#define S_ArgCompactIVs 0x04
#define S_ArgRandLow 0x08
#define S_ArgRandHigh 0x0C

#define S_PlayerId 0x10
#define S_TrainerId 0x14
#define S_GenderAbilityByte 0x18
#define S_PokeRand 0x1C ; generated for each individual poke
#define S_IVs 0x20 ; generated for each individual poke
#define S_EVBits 0x24
#define S_EVsIndex 0x28
#define S_EVsPerStat 0x2C
#define S_Item 0x30
#define STACK_SIZE 0x34

#define PUSH_SIZE (5 * 4) ; r4-r7, lr

#define ARG_OFFSET (STACK_SIZE + PUSH_SIZE)

#define ARG_HeapId (ARG_OFFSET + 0x00)
#define ARG_PokeDataOffsets (ARG_OFFSET + 0x04) ; [0] = statModifiers, [1] = item, [2] = moves
#define ARG_TeamRand (ARG_OFFSET + 0x08) ; updated through each poke, used to generate S_PokeRand
#define ARG_TrainerDataPtr (ARG_OFFSET + 0x0C)
#define ARG_TempPokePtr (ARG_OFFSET + 0x10)

; r0 pokeDataPtr
; r1 pokeDataSize
; r2 playerId
; r3 trainerId
; ARG_HeapId
; ARG_PokeDataOffsets
; ARG_TeamRand
; ARG_TrainerDataPtr
; ARG_TempPokePtr

    push    {r4-r7, lr}
    sub     sp, #STACK_SIZE
    
    mov     r6, r0 ; pokeDataPtr
    mov     r7, r1 ; pokeDataSize
    str     r2, [sp, #S_PlayerId]
    str     r3, [sp, #S_TrainerId]

    #printf("    ARM9::TrTool_MakePokeFromData (LR=0x%08X)", lr)
    #printf("        pokeDataPtr=0x%08X", r6)
    #printf("        pokeDataSize=%d", r7)
    #printf("        playerId=0x%08X", ldr [sp, #S_PlayerId])
    #printf("        trainerId=%d", ldr [sp, #S_TrainerId])

    mov     r0, r6
    bl      ARM9::TrainerPoke_GetGenderAbilityByte ; byte value where lower 4 bits are gender and upper 4 bits are ability
    str     r0, [sp, #S_GenderAbilityByte]
    mov     r2, r0
    ldr     r0, [sp, #ARG_HeapId]
    str     r0, [sp, #S_ArgHeapId]
    ldrb    r0, [r6, #TrainerPoke.basicFlags]
    mov     r1, #TrainerPoke_BasicFlags.formMask
    and     r1, r0
    ldrh    r0, [r6, #TrainerPoke.species]
    add     r3, sp, #ARG_TeamRand ; return value
    bl      ARM9::TrTool_GetRandomPersonal

    #printf("        teamRand=%d", ldr [sp, #ARG_TeamRand])
    #printf("        species=%d", ldrh [r6, #TrainerPoke.species])
    #printf("        basicFlags=0x%02X", ldrb [r6, #TrainerPoke.basicFlags])
    #printf("        level=%d", ldrb [r6, #TrainerPoke.level])
    
    mov     r0, #0
    str     r0, [sp, #S_PokeRand]
    str     r0, [sp, #S_IVs]
    
CheckPokesHaveStatModifiers:
    add     r1, sp, #ARG_PokeDataOffsets
    ldrb    r1, [r1, #0x00] ; statModifiers
    #printf("        statModifierOffset=%d", r1)
    cmp     r1, #0
    beq     AddLevelToRand
    
AddIVsToRand:
    ldrb    r1, [r6, r1]
    #read_bits(r1, #TrainerPoke_StatModifiers.ivBit, #TrainerPoke_StatModifiers.ivSize)
    str     r1, [sp, #S_IVs]
    add     r0, r1
    
AddLevelToRand:
    ldrb    r1, [r6, #TrainerPoke.level]
    add     r0, r1
    
AddSpeciesToRand:
    ldrh    r1, [r6, #TrainerPoke.species]
    add     r0, r1
    
AddTrainerIdToRand:
    ldr     r1, [sp, #S_TrainerId]
    add     r0, r1
    
; this is a major difference compared to vanilla...
; we randomize using the player's trainer ID to keep each run unique
AddPlayerIdToRand:
    ldr     r1, [sp, #S_PlayerId]
    add     r0, r1
    ldr     r5, [sp, #ARG_TrainerDataPtr]
    ldrb    r5, [r5, #TrainerData.class] ; r5 := trainerClass
    add     r5, #8 ; minimum of 8 for entropy
    mov     r1, #0
    
LcgSeedLoop:
    ldr     r2, =RAND_LCG_Multiplier_Low
    ldr     r3, =RAND_LCG_Multiplier_High
    blx     ARM9::Multiply64 ; r0 := rand * mul
    ldr     r2, =RAND_LCG_Increment
    add     r0, r2 ; r0 += inc
    ldr     r2, =0 ; avoids updating the c bit
    adc     r1, r2
    lsr     r2, r1, #16 ; r2 := high(rand), ignored/overwritten until the last
    
    sub     r5, #1
    cmp     r5, #0
    bgt     LcgSeedLoop
    
FinalizePokeRand:
    lsl     r0, r2, #8
    ldr     r1, [sp, #ARG_TeamRand]
    add     r0, r1
    str     r0, [sp, #S_ArgRandLow] ; argRandLow
    
MakeCompactIVs:
    ldr     r0, [sp, #S_IVs]
    mov     r1, r0
    lsl     r2, r0, #5
    orr     r1, r2
    lsl     r2, r0, #10
    orr     r1, r2
    lsl     r2, r0, #15
    orr     r1, r2
    lsl     r2, r0, #20
    orr     r1, r2
    lsl     r2, r0, #25
    orr     r1, r2
    str     r1, [sp, #S_ArgCompactIVs] ; argCompactIVs
    
    mov     r3, #0 ; argIdLow (always 0x00000000)
    str     r3, [sp, #S_ArgRandHigh] ; argRandHigh (always 0x00000000)
    mvn     r0, r3
    str     r0, [sp, #S_ArgIdHigh] ; argIdHigh (always 0xFFFFFFFF)
    
    ldr     r0, [sp, #ARG_TempPokePtr]
    ldrh    r1, [r6, #TrainerPoke.species]
    ldrb    r2, [r6, #TrainerPoke.level]
    bl      ARM9::PokeTool_SetupEx
    
CheckPokeHasStatModifiers:
    #printf("        check poke has stat modifiers")
    add     r4, sp, #ARG_PokeDataOffsets
    ldrb    r4, [r4, #0x00] ; stat modifiers offset
    cmp     r4, #0
    beq     CheckPokeHasItem
    
    ; EV spread    
    ldrh    r0, [r6, r4]
    #read_bits(r0, #TrainerPoke_StatModifiers.evFlagBit, #TrainerPoke_StatModifiers.evFlagSize)
    str     r0, [sp, #S_EVBits]
    
#if DEBUG
    #printf("            evFlags=0x%02X", r0)
    ldr     r0, [sp, #S_EVBits]
#endif
    
    mov     r1, #0 ; count
    mov     r3, #0 ; stat
    
    ; obtain the number of stats to split 510 EVs across
EVSpreadGetCountLoop:
    mov     r2, #1
    lsl     r2, r3 ; r2 := 1 << stat
    tst     r0, r2
    beq     EVSpreadGetCountLoop_End
    add     r1, #1
EVSpreadGetCountLoop_End:
    add     r3, #1 ; increment
    cmp     r3, #6
    bcc     EVSpreadGetCountLoop
    
    ; ensure r2 is min 2
    cmp     r1, #2
    bcs     GetEVsPerStat
    mov     r1, #2 ; min of 2 for max of 252 per stat (avoids divide by 0 and issues if only 1 stat is selected)
    
GetEVsPerStat:
    mov     r0, #255
    lsl     r0, #1 ; 510
    bl      ARM9::DivideModUnsigned
    cmp     r0, #252
    bls     SetEVsPerStat ; max of 252 per stat
    mov     r0, #252
    
SetEVsPerStat:
    str     r0, [sp, #S_EVsPerStat]
    #printf("            evsPerStat=%d", r0)
    
    mov     r0, #0 ; stat
    str     r0, [sp, #S_EVsIndex]
    
SetEVsLoop:
    ldr     r1, [sp, #S_EVsIndex]
    mov     r0, #1
    lsl     r0, r1
    ldr     r2, [sp, #S_EVBits]
    tst     r2, r0
    beq     SetEVsLoop_End
    
    ldr     r0, [sp, #ARG_TempPokePtr]
    add     r1, #PF_HPEVs ; param
    ldr     r2, [sp, #S_EVsPerStat]
    bl      ARM9::Poke_SetParam
    
    #printf("            set EVs of stat %d to %d", ldr [sp, #S_EVsIndex], ldr [sp, #S_EVsPerStat])
    
SetEVsLoop_End:
    ldr     r0, [sp, #S_EVsIndex]
    add     r0, #1
    str     r0, [sp, #S_EVsIndex]
    cmp     r0, #6
    bcc     SetEVsLoop
    
CheckHasNature:
    #printf("            check has nature")
    
    ; Nature (confirm first that we have natures)
    ldr     r0, [sp, #ARG_TrainerDataPtr]
    bl      ARM9::TrTool_PokesHaveNatures
    cmp     r0, #FALSE
    beq     CheckPokeHasItem
    
    ldr     r0, [sp, #ARG_TempPokePtr]
    ldrh    r2, [r6, r4]
    lsl     r2, #(32 - (TrainerPoke_StatModifiers.natureBit + TrainerPoke_StatModifiers.natureSize))
    lsr     r2, #(32 - TrainerPoke_StatModifiers.natureSize)
    mov     r1, #PF_Nature
    bl      ARM9::Poke_SetParam
    
#if DEBUG
    ldrh    r0, [r6, r4]
    #read_bits(r0, TrainerPoke_StatModifiers.natureBit, TrainerPoke_StatModifiers.natureSize)
    #printf("                Set Nature to %d", r0)
#endif

CheckPokeHasItem:
    #printf("        check poke has item")
    add     r4, sp, #ARG_PokeDataOffsets
    ldrb    r4, [r4, #0x01] ; item offset
    #printf("        itemOffset=%d", r4)
    cmp     r4, #0
    beq     CheckPokeHasMoves
    
    ldr     r0, [sp, #ARG_TempPokePtr]
    mov     r1, #PF_Item
    ldrh    r2, [r6, r4]
    bl      ARM9::Poke_SetParam
    
    #printf("            Set item to %d", ldrh [r6, r4])
    
CheckPokeHasMoves:
    #printf("        check poke has moves")
    add     r4, sp, #ARG_PokeDataOffsets
    ldrb    r4, [r4, #0x02] ; moves offset
    #printf("        movesOffset=%d", r4)
    cmp     r4, #0
    beq     FinishSetup
    
    mov     r5, #0 ; iteration
MovesLoop_Start:
    ldr     r0, [sp, #ARG_TempPokePtr]
    mov     r1, #PF_Move1
    add     r1, r5
    ldrh    r2, [r6, r4]
    bl      ARM9::Poke_SetParam
    
    add     r5, #1
    #printf("            move%d set to %d", r5, ldrh [r6, r4])
    
    add     r4, #2
    cmp     r5, #4
    bcc     MovesLoop_Start
    
FinishSetup:
    #printf("        finishing setup...")
    mov     r0, r6
    bl      ARM9::TrainerPoke_GetFormId
    mov     r2, r0
    ldr     r0, [sp, #S_TrainerId]
    ldr     r1, [sp, #ARG_TempPokePtr]
    ldr     r3, [sp, #S_GenderAbilityByte]
    #printf("        formId=%d", r2)
    #printf("        genderAbilityByte=0x%02X", r3)
    bl      ARM9::TrTool_FinishPokeSetup
    #printf("        finished setup!")
    
Return:
    add     sp, #STACK_SIZE
    pop     {r4-r7, pc}
    