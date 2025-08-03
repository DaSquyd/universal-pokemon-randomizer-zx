#define SP_FacingPokeId 0x00

    push    {r3-r7, lr}
    sub     sp, #0x04
    mov     r4, r1 ; server flow
    mov     r6, r2 ; user poke id
    
    ; check that poke is ability owner
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r6, r0
    bne     Return
    
    mov     r0, r4 ; server flow
    mov     r1, r6 ; user poke id
    bl      Battle::Handler_PokeIDToPokePos
    mov     r5, r0 ; r5 := user poke pos
    
    mov     r0, r4 ; server flow
    bl      Battle::GetBattleStyle
    mov     r1, r5 ; user poke pos
    bl      Battle::MainModule_GetFacingPokemon
    str     r0, [sp, #SP_FacingPokeId]
    mov     r1, r0 ; target poke id
    mov     r0, r4 ; server flow
    bl      Battle::GetPoke
    mov     r5, r0 ; r5 := target battle poke
    
    mov     r0, r4 ; server flow
    mov     r1, r6 ; user poke id
    bl      Battle::GetPoke
    bl      Battle::IsPokeFainted
    cmp     r0, #FALSE
    bne     Return
    
    mov     r0, r5 ; target battle poke
    bl      Battle::IsPokeFainted
    cmp     r0, #FALSE
    bne     Return
    
PersonalGroundedCheck:
    mov     r0, r5 ; target battle poke
    bl      Battle::GetPokeSpecies
    mov     r7, r0
    
    mov     r0, r5 ; target battle poke
    mov     r1, #BPV_Form
    bl      Battle::GetPokeStat
    mov     r1, r0
    mov     r0, r7
    bl      ARM9::LoadPersonalB2W2
    mov     r1, #16 ; grounded
    bl      ARM9::ReadPokePersonalData
    cmp     r0, #FALSE
    bne     Return
    
DoWork:
    ; push work
    mov     r0, r4 ; server flow
    mov     r1, #HE_AddCondition
    mov     r2, r6 ; user pokeId
    bl      Battle::Handler_PushWork
    mov     r7, r0 ; r7 := work
    
    ; ability popup
    ldr     r0, [r7, #HandlerParam_AddCondition.header]
    mov     r1, #BHP_AbilityPopup
    orr     r0, r1
    str     r0, [r7, #HandlerParam_AddCondition.header]
    
    ; set condition type
    mov     r0, #MC_Telekinesis
    str     r0, [r7, #HandlerParam_AddCondition.conditionType]
    
    ; create condition and set ptr on work
    mov     r0, r6 ; user poke id
    mov     r1, #(ABILITY_TRACTOR_BEAM_TURNS)
    bl      Battle::MakeCondition_PokeTurns
    str     r0, [r7, #HandlerParam_AddCondition.conditionPtr]
    
    ; set always
    mov     r0, #TRUE
    strb    r0, [r7, #HandlerParam_AddCondition.fAlways]
    
    ; set target poke id
    ldr     r0, [sp, #SP_FacingPokeId]
    strb    r0, [r7, #HandlerParam_AddCondition.pokeId]
    
    ; set string
    mov     r0, r7
    add     r0, #HandlerParam_AddCondition.exStr
    mov     r1, #2 ; file id
    ldr     r2, =1140
    bl      Battle::Handler_StrSetup
    
    ; add arg to string (target poke id)
    mov     r0, r7
    add     r0, #HandlerParam_AddCondition.exStr
    ldr     r1, [sp, #SP_FacingPokeId]
    bl      Battle::Handler_AddArg
    
    ; pop work
    mov     r0, r4
    mov     r1, r7
    bl      Battle::Handler_PopWork
    
Return:
    add     sp, #0x04
    pop     {r3-r7, pc}