    push    {r4-r7, lr}
    sub     sp, #0x14
    mov     r5, r0
    
    mov     r0, #0
    str     r0, [sp, #0x00]
    
    ldr     r0, =ServerFlow.numActOrder
    mov     r4, r1
    ldrb    r1, [r5, r0]
    cmp     r4, r1
    bcs     Label_0x0219FAE4
    mov     r1, r0
    add     r1, #94
    add     r7, r5, r1
    mov     r1, r0
    add     r1, #98
    add     r1, r5, r1
    str     r1, [sp, #0x08]
    add     r1, r5, r0
    add     r0, r5, r0
    str     r1, [sp, #0x0C]
    str     r0, [sp, #0x10]

Label_0x0219FA3A:
    ldr     r0, [sp, #0x08]
    lsl     r6, r4, #4
    add     r0, r6
    bl      Battle::BattleAction_GetActionType
    ldr     r1, [sp, #0x00]
    cmp     r1, #BA_Rotate
    bne     CheckIsFightAction
    cmp     r0, #BA_Rotate
    beq     CheckIsFightAction
    
    mov     r0, r5
    bl      Battle::ServerControl_CheckActivation
    
    mov     r3, #1
    b       UpdateActionPriority
    
CheckIsFightAction:
    cmp     r0, #BA_Fight
    bne     ProcessActionOrder
    
    mov     r3, #0
    
UpdateActionPriority:
    mov     r0, r5
    add     r1, r7, r6
    ldr     r2, [sp, #0x0C]
    ldrb    r2, [r2, #0x00]
    sub     r2, r2, r4
    bl      Battle::ServerFlow_UpdateActionPriority

ProcessActionOrder:
    mov     r0, r5
    add     r1, r7, r6
    bl      Battle::ActionOrder_Proc
    str     r0, [sp, #0x00]
    mov     r0, r5
    bl      Battle::ServerControl_CheckExpGet
    str     r0, [sp, #0x04]
    ldr     r0, [r5, #0x08]
    mov     r1, #0
    bl      Battle::PokeCon_GetPoke
    
    mov     r0, r5
    bl      Battle::ServerControl_CheckMatchup
    mov     r6, r0
    ldr     r0, [r5, #0x08]
    mov     r1, #0
    bl      Battle::PokeCon_GetPoke
    cmp     r6, #0
    beq     Label_0x0219FAA8
    mov     r0, #4
    str     r0, [r5, #0x14]
    add     sp, #0x14
    add     r0, r4, #1
    pop     {r4-r7, pc}

Label_0x0219FAA8:
    ldr     r0, [r5, #0x14]
    cmp     r0, #6
    bne     Label_0x0219FAB4
    add     sp, #0x14
    add     r0, r4, #1
    pop     {r4-r7, pc}

Label_0x0219FAB4:
    cmp     r0, #1
    bne     Label_0x0219FABE
    add     sp, #0x14
    add     r0, r4, #1
    pop     {r4-r7, pc}

Label_0x0219FABE:
    ldr     r0, [sp, #0x04]
    cmp     r0, #0
    beq     Label_0x0219FADA
    ldr     r0, [r5, #0x08]
    mov     r1, #0
    bl      Battle::PokeCon_GetPoke
    mov     r0, #3
    str     r0, [r5, #0x14]
    add     sp, #0x14
    add     r0, r4, #1
    pop     {r4-r7, pc}

Label_0x0219FADA:
    ldr     r0, [sp, #0x10]
    add     r4, r4, #1
    ldrb    r0, [r0, #0x00]
    cmp     r4, r0
    bcc     Label_0x0219FA3A

Label_0x0219FAE4:
    ldr     r0, [r5, #0x14]
    cmp     r0, #0
    bne     Label_0x0219FB5A
    mov     r0, r5
    bl      Battle::ServerControl_TurnCheck
    lsl     r0, r0, #24
    lsr     r4, r0, #24
    mov     r0, r5
    bl      Battle::ServerControl_CheckMatchup
    cmp     r0, #0
    beq     Label_0x0219FB0C
    mov     r0, #4
    str     r0, [r5, #0x14]
    ldr     r0, =0x0782
    add     sp, #0x14
    ldrb    r0, [r5, r0]
    pop     {r4-r7, pc}

Label_0x0219FB0C:
    cmp     r4, #0
    beq     Label_0x0219FB1C
    mov     r0, #3
    str     r0, [r5, #0x14]
    ldr     r0, =0x0782
    add     sp, #0x14
    ldrb    r0, [r5, r0]
    pop     {r4-r7, pc}

Label_0x0219FB1C:
    mov     r0, #62
    lsl     r0, r0, #4
    add     r0, r5, r0
    mov     r1, #0
    mov     r6, #0
    bl      BattleServer::FaintRecord_GetCount
    mov     r4, r0
    mov     r0, r5
    bl      Battle::Handler_IsPosOpenForRevivedPoke
    cmp     r0, #0
    bne     Label_0x0219FB3A
    cmp     r4, #0
    beq     Label_0x0219FB58

Label_0x0219FB3A:
    ldr     r4, =0x04CE
    mov     r0, r5
    add     r1, r5, r4
    bl      Battle::ServerFlow_ReqChangePokeForServer
    mov     r0, r5
    add     r1, r5, r4
    bl      Battle::ServerDisplay_IllusionSet
    mov     r0, #2
    str     r0, [r5, #0x14]
    ldr     r0, =0x0782
    add     sp, #0x14
    ldrb    r0, [r5, r0]
    pop     {r4-r7, pc}

Label_0x0219FB58:
    str     r6, [r5, #0x14]

Label_0x0219FB5A:
    ldr     r0, =0x0782
    ldrb    r0, [r5, r0]
    add     sp, #0x14
    pop     {r4-r7, pc}