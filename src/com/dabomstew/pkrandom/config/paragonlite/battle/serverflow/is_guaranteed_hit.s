    push    {r4-r6, lr}
    mov     r5, r0
    mov     r4, r1
    mov     r6, r2
    
    
CheckAttacker:
    mov     r0, r4
    bl      Battle::GetPokeId
    mov     r2, r0
    ldr     r0, =(ServerFlow.posPoke)
    ldr     r1, [r5, #(ServerFlow.mainModule)]
    add     r0, r5
    bl      BattleServer::PosPoke_IsExistFrontPos
    cmp     r0, #FALSE
    beq     CheckDefender
    
    mov     r0, r4
    mov     r1, #BPV_EffectiveAbility
    bl      Battle::GetPokeStat
    cmp     r0, #099 ; No Guard
    beq     ReturnTrue
    
    
CheckDefender:
    mov     r0, r6
    bl      Battle::GetPokeId
    mov     r2, r0
    ldr     r0, =(ServerFlow.posPoke)
    ldr     r1, [r5, #(ServerFlow.mainModule)]
    add     r0, r5
    bl      BattleServer::PosPoke_IsExistFrontPos
    cmp     r0, #FALSE
    beq     CheckMoveCondition_MustHit
    
    mov     r0, r6
    mov     r1, #BPV_EffectiveAbility
    bl      Battle::GetPokeStat
    cmp     r0, #099 ; No Guard
    beq     ReturnTrue
    
    cmp     r0, #512 ; Colossal
    beq     ReturnTrue
    
    
CheckMoveCondition_MustHit:
    mov     r0, r4
    mov     r1, #MC_MustHit
    bl      Battle::CheckCondition
    cmp     r0, #FALSE
    bne     ReturnTrue
    
    
CheckMoveCondition_LockOn:
    mov     r0, r4
    mov     r1, #MC_LockOn
    bl      Battle::CheckCondition
    cmp     r0, #FALSE
    beq     ReturnFalse
    
    mov     r0, r4
    mov     r1, #MC_LockOn
    bl      Battle::GetDisabledMove
    mov     r4, r0
    
    mov     r0, r6
    bl      Battle::GetPokeId
    cmp     r4, r0
    bne     ReturnFalse
    
    
ReturnTrue:
    mov     r0, #TRUE
    pop     {r4-r6, pc}

ReturnFalse:
    mov     r0, #FALSE
    pop     {r4-r6, pc}
    