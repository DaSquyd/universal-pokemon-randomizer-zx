    push    {r3-r7,lr}
    mov     r5, r1 ; r5 := server flow
    mov     r6, r2 ; r6 := user poke id

    ; check if poke is the ability user
    mov     r0, #VAR_PokeId
    bl      Battle::EventVar_GetValue
    cmp     r6, r0
    bne     Return

    ; check if a spin/roll move was used
    mov     r0, #VAR_MoveId
    bl      Battle::EventVar_GetValue
    mov     r1, #MF_RollSpin
    cmp     r0, #FALSE
    beq     Return

    ; get poke position on the field
    mov     r0, r5 ; r0 := server flow
    mov     r1, r6 ; r1 := user poke id
    bl      Battle::Handler_PokeIDToPokePos
    mov     r2, r0 ; r2 := user poke pos

    ; check if user is fainted
    mov     r0, r2 ; r0 := server flow
    mov     r1, r6 ; r1 := user poke id
    bl      Battle::GetPoke
    bl      Battle::IsPokeFainted
    cmp     r0, #FALSE
    bne     Return

DoWork:
    ; push work
    mov     r0, r2 ; r0 := server flow
    mov     r2, r1 ; user poke id
    mov     r1, #HE_AddCondition
    bl      Battle::Handler_PushWork
    mov     r7, r0 ; r7 := work

    ; ability popup
    ldr     r0, [r7, #HanderParam_AddCondition.header]
    mov     r1, #BHP_AbilityPopup
    orr     r0, r1
    str     r0, [r7, #HanderParam_AddCondition.header]

    ; set condition type
    mov     r0, #SOMEHOW_CHARGE
    str     r0, [r7, #HanderParam_AddCondition.conditionType]

    ; create condition and set ptr on work
    mov     r0, r6 ; r0 := user poke id
    mov     r1, #(ABILITY_DYNAMO_CHARGE_TURNS)
    bl      Battle::MakeCondition_PokeTurns
    str     r0, [r7, #HanderParam_AddCondition.conditionPtr]

    ; set always
    mov     r0, #True
    strb    r0, [r7, #HanderParam_AddCondition.fAlways]

    ; set target poke id



Return:
    pop     {r3-r7,lr}