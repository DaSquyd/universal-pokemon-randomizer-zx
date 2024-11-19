#DEFINE ABILITY_STAT 0x11

#DEFINE GHOST_TYPE 7

; Abilities
#DEFINE SHADOW_TAG 23
#DEFINE MAGNET_PULL 42
#DEFINE RUN_AWAY 50
#DEFINE ARENA_TRAP 71

; Conditions
#DEFINE TRAPPED_CONDITION 8
#DEFINE INGRAIN_CONDITION 21
#DEFINE NO_ESCAPE_CONDITION 22

#DEFINE VAR_28 0x00
#DEFINE VAR_24 0x04
#DEFINE POKE 0x08
#DEFINE POKE_COUNT 0x0C
#DEFINE VAR_18 0x10
#DEFINE ARG_14 0x14
#DEFINE __MAX__ ARG_14

    push    {r4-r7, lr}
    sub     sp, #__MAX__
    
    mov     r6, r1
    mov     r5, r0
    
    mov     r0, r6
    mov     r1, #GHOST_TYPE
    bl      Battle::PokeHasType
    cmp     r0, #0
    bne     ReturnNotTrapped
    
CheckRunAway:
    mov     r0, r6
    mov     r1, #ABILITY_STAT
    bl      Battle::GetPokeStat
    cmp     r0, #RUN_AWAY
    bne     CheckAllForTrap
    
ReturnNotTrapped:
    mov     r0, #4
    add     sp, #__MAX__
    pop     {r4-r7, pc}
    
CheckAllForTrap:
    mov     r0, r6
    mov     r1, r5
    str     r2, [sp, #VAR_28]
    str     r3, [sp, #VAR_24]
    bl      Battle::GetPokeId
    mov     r2, r0
    
    ldr     r0, [r5, #0x00]
    ldr     r1, [r5, #0x04]
    bl      Battle::PokeIdToPos
    mov     r2, #1
    mov     r3, r0
    lsl     r2, #8
    orr     r2, r3
    lsl     r2, #16
    
    ldr     r0, [r5, #0x00]
    ldr     r1, [r5, #0x04]
    lsr     r2, #16
    add     r3, sp, #VAR_18
    bl      Battle::GetActiveFoes
    str     r0, [sp, #POKE_COUNT]
    mov     r7, #0
    cmp     r0, #0
    bls     CheckNoEscapeCondition
    
LoopStart:
    add     r1, sp, #VAR_18
    ldrb    r1, [r1, r7]
    ldr     r0, [r5, #0x04]
    bl      Battle::PokeCon_GetPoke
    mov     r1, #ABILITY_STAT
    str     r0, [sp, #POKE]
    bl      Battle::GetPokeStat
    lsl     r0, #16
    lsr     r4, r0, #16
    ldr     r0, [sp, #POKE]
    bl      Battle::GetPokeId
    cmp     r4, #SHADOW_TAG
    bne     Loop_CheckArenaTrap
    
    mov     r0, r5
    mov     r1, r6
    bl      Battle::DoesPokeHaveShadowTag
    cmp     r0, #0
    beq     Loop_CheckArenaTrap
    
    ldr     r0, [sp, #POKE]
    bl      Battle::GetPokeId
    ldr     r1, [sp, #VAR_28]
    strb    r0, [r1]
    ldr     r0, [sp, #VAR_24]
    add     sp, #ARG_14
    strh    r4, [r0]
    mov     r0, #0
    POP     {r4-r7, pc}
    
Loop_CheckArenaTrap:
    cmp     r4, #ARENA_TRAP
    bne     Loop_CheckMagnetPull
    
    mov     r0, r5
    mov     r1, r6
    bl      Battle::IsPokeGrounded
    cmp     r0, #0
    beq     Loop_CheckMagnetPull
    
    ldr     r0, [sp, #POKE]
    bl      Battle::GetPokeId
    ldr     r1, [sp, #VAR_28]
    strb    r0, [r1]
    ldr     r0, [sp, #VAR_24]
    add     sp, #ARG_14
    strh    r4, [r0]
    mov     r0, #0
    pop     {r4-r7, pc}
    
Loop_CheckMagnetPull:
    cmp     r4, #MAGNET_PULL
    bne     LoopIncrement
    mov     r0, r5
    mov     r1, r6
    bl      Battle::IsPokeSteelType
    cmp     r0, #0
    beq     LoopIncrement
    ldr     r0, [sp, #POKE]
    bl      Battle::GetPokeId
    ldr     r1, [sp, #VAR_28]
    strb    r0, [r1]
    ldr     r0, [sp, #VAR_24]
    add     sp, #ARG_14
    strh    r4, [r0]
    mov     r0, #0
    pop     {r4-r7, pc}
    
LoopIncrement:
    add     r0, r7, #1
    lsl     r0, #24
    lsr     r7, r0, #24
    ldr     r0, [sp, #POKE_COUNT]
    cmp     r7, r0
    bcc     LoopStart
    
CheckNoEscapeCondition:
    mov     r0, r6
    mov     r1, #NO_ESCAPE_CONDITION
    bl      Battle::CheckCondition
    cmp     r0, #0
    bne     ReturnConditionTrapped
    
    mov     r0, r6
    mov     r1, #TRAPPED_CONDITION
    bl      Battle::CheckCondition
    cmp     r0, #0
    bne     ReturnConditionTrapped
    
    mov     r0, r6
    mov     r1, #INGRAIN_CONDITION
    bl      Battle::CheckCondition
    cmp     r0, #0
    beq     ReturnNotTrapped
    
ReturnConditionTrapped:
    mov     r0, r6
    bl      Battle::GetPokeId
    ldr     r1, [sp, #VAR_28]
    strb    r0, [r1]
    ldr     r0, [sp, #VAR_24]
    mov     r1, #0
    strh    r1, [r0]
    add     sp, #__MAX__
    mov     r0, #3
    pop     {r4-r7, pc}
    