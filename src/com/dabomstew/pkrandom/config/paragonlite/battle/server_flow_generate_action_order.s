    push    {r3-r7, lr}
    sub     sp, #0x34
    str     r0, [sp, #0x00]
    mov     r0, #0
    str     r0, [sp, #0x10]
    mov     r0, #0
    str     r1, [sp, #0x04]
    str     r2, [sp, #0x08]
    str     r0, [sp, #0x1C]
    mov     r7, #0

Label_0x021A00F8:
    ldr     r0, [sp, #0x00]
    ldr     r1, [sp, #0x1C]
    ldr     r0, [r0, #0x00]
    bl      Battle::BattleServer_GetClientWork
    mov     r6, r0
    beq     Label_0x021A01A8
    ldr     r0, [sp, #0x04]
    ldr     r1, [sp, #0x1C]
    bl      Battle::BattleServer_GetNumActPoke
    str     r0, [sp, #0x14]
    mov     r0, #0
    str     r0, [sp, #0x18]
    ldr     r0, [sp, #0x14]
    mov     r5, #0
    cmp     r0, #0
    bls     Label_0x021A01A8

Label_0x021A011C:
    ldr     r0, [sp, #0x08]
    lsl     r1, r7, #4
    add     r4, r0, r1
    ldr     r0, [sp, #0x04]
    ldr     r1, [sp, #0x1C]
    ldr     r2, [sp, #0x18]
    bl      Battle::BattleServer_GetAction
    str     r0, [r4, #0x04]
    add     r0, r4, #4
    bl      Battle::Action_IsDeplete
    cmp     r0, #0
    bne     Label_0x021A01A8
    add     r0, r4, #4
    bl      Battle::BattleAction_GetActionType
    cmp     r0, #3
    beq     Label_0x021A016A
    cmp     r0, #6
    bne     Label_0x021A0178
    ldr     r0, [r4, #0x04]
    lsl     r0, r0, #25
    lsr     r0, r0, #29
    bl      Battle::RotationToPartyIdx
    mov     r5, r0
    ldr     r0, [r6, #0x04]
    mov     r1, r5
    bl      Battle::BattleParty_GetPartyMember
    str     r0, [r4, #0x00]
    mov     r0, #1
    str     r0, [sp, #0x10]
    b       Label_0x021A0188

Label_0x021A016A:
    ldr     r1, [r4, #0x04]
    ldr     r0, [r6, #0x04]
    lsl     r1, r1, #25
    lsr     r1, r1, #29
    b       Label_0x021A017C

Label_0x021A0178:
    ldr     r0, [r6, #0x04]
    mov     r1, r5

Label_0x021A017C:
    bl      Battle::BattleParty_GetPartyMember
    str     r0, [r4, #0x00]
    add     r5, #1

Label_0x021A0188:
    ldr     r0, [sp, #0x1C]
    strb    r0, [r4, #0x0C]
    mov     r0, #0
    strb    r0, [r4, #0x0D]
    strb    r0, [r4, #0x0E]
    add     r7, #1
    ldr     r0, [sp, #0x18]
    add     r0, r0, #1
    lsl     r0, r0, #24
    lsr     r1, r0, #24
    str     r1, [sp, #0x18]
    ldr     r0, [sp, #0x14]
    cmp     r1, r0
    bcc     Label_0x021A011C

Label_0x021A01A8:
    ldr     r0, [sp, #0x1C]
    add     r0, r0, #1
    lsl     r0, r0, #24
    lsr     r0, r0, #24
    str     r0, [sp, #0x1C]
    cmp     r0, #4
    bcc     Label_0x021A00F8
    mov     r0, #0
    str     r0, [sp, #0x0C]
    cmp     r7, #0
    bls     Label_0x021A02BC
    ldr     r0, =0x0682
    str     r0, [sp, #0x28]
    sub     r0, #56
    str     r0, [sp, #0x28]

Label_0x021A01CC:
    ldr     r1, [sp, #0x00]
    ldr     r0, =0x1D78
    add     r0, r1, r0
    ldr     r1, [sp, #0x28]
    bl      Battle::HEManager_PushState
    str     r0, [sp, #0x20]
    ldr     r0, [sp, #0x0C]
    lsl     r6, r0, #4
    ldr     r0, [sp, #0x08]
    add     r4, r0, r6
    ldr     r0, [r0, r6]
    str     r0, [sp, #0x24]
    ldr     r0, [r4, #0x04]
    lsl     r0, r0, #28
    lsr     r0, r0, #28
    cmp     r0, #8
    bhi     Label_0x021A023C
    #SWITCH r0
    #CASE Label_0x021A0238
    #CASE Label_0x021A0236
    #CASE Label_0x021A022A
    #CASE Label_0x021A0226
    #CASE Label_0x021A020E
    #CASE Label_0x021A0234
    #CASE Label_0x021A022E
    #CASE Label_0x021A0232
    #CASE Label_0x021A02AE

Label_0x021A020E:
    ldr     r0, [sp, #0x00]
    ldr     r0, [r0, #0x04]
    bl      Battle::MainModule_GetBattleType
    cmp     r0, #0
    bne     Label_0x021A0222
    ldrb    r0, [r4, #0x0C]
    cmp     r0, #1
    bne     Label_0x021A0222
    b       Label_0x021A023C

Label_0x021A0222:
    mov     r5, #4
    b       Label_0x021A023E

Label_0x021A0226:
    mov     r5, #3
    b       Label_0x021A023E

Label_0x021A022A:
    mov     r5, #2
    b       Label_0x021A023E

Label_0x021A022E:
    mov     r5, #1
    b       Label_0x021A023E

Label_0x021A0232:
    b       Label_0x021A023C

Label_0x021A0234:
    b       Label_0x021A023C

Label_0x021A0236:
    b       Label_0x021A023C

Label_0x021A0238:
    ldr     r0, =0x03002000 ; NEW - includes default value for Quash
    b       Label_0x021A02AC

Label_0x021A023C:
    mov     r5, #0

Label_0x021A023E:
    ldr     r0, [r4, #0x04]
    lsl     r0, r0, #28
    lsr     r0, r0, #28
    cmp     r0, #1
    bne     Label_0x021A026E
    ldr     r0, [sp, #0x08]
    add     r0, r0, r6
    ldr     r0, [r0, #0x04]
    lsl     r1, r0, #28
    lsr     r1, r1, #28
    cmp     r1, #1
    bne     Label_0x021A0260
    lsl     r0, r0, #9
    lsr     r0, r0, #16
    lsl     r0, r0, #16
    lsr     r1, r0, #16
    b       Label_0x021A0262

Label_0x021A0260:
    mov     r1, #0

Label_0x021A0262:
    ldr     r0, [sp, #0x00]
    ldr     r2, [sp, #0x24]
    bl      Battle::ServerEvent_GetMovePriority
    mov     r6, r0
    b       Label_0x021A027C

Label_0x021A026E:
    cmp     r0, #5
    beq     Label_0x021A0276
    cmp     r0, #7
    bne     Label_0x021A027A

Label_0x021A0276:
    mov     r6, #7
    b       Label_0x021A027C

Label_0x021A027A:
    mov     r6, #0

Label_0x021A027C:
    ldr     r0, [sp, #0x00]
    ldr     r1, [sp, #0x24]
    mov     r2, #1
    bl      Battle::ServerEvent_CalculateSpeed
    str     r0, [sp, #0x2C]
    ldr     r1, [sp, #0x00]
    ldr     r0, =0x1D78
    ldr     r2, =0x0682
    add     r0, r1, r0
    ldr     r1, [sp, #0x20]
    bl      Battle::HEManager_PopState
    lsl     r2, r6, #26
    ldr     r1, [sp, #0x2C] ; speed
    ldr     r0, =0x1FFF
    lsr     r2, r2, #10
    and     r1, r0
    lsl     r0, r5, #29 ; action
    lsr     r0, r0, #7
    orr     r2, r0
    ldr     r0, =0x02002000
    orr     r0, r2
    orr     r0, r1

Label_0x021A02AC:
    str     r0, [r4, #0x08]

Label_0x021A02AE:
    ldr     r0, [sp, #0x0C]
    add     r0, r0, #1
    lsl     r0, r0, #24
    lsr     r0, r0, #24
    str     r0, [sp, #0x0C]
    cmp     r0, r7
    bcc     Label_0x021A01CC

Label_0x021A02BC:
    ldr     r0, [sp, #0x08]
    mov     r1, r7
    bl      Battle::SortActionSub
    ldr     r0, [sp, #0x10]
    cmp     r0, #0
    bne     Label_0x021A032A
    mov     r5, #0
    cmp     r7, #0
    bls     Label_0x021A0322

Label_0x021A02D0:
    ldr     r0, [sp, #0x08]
    lsl     r1, r5, #4
    add     r4, r0, r1
    ldr     r0, [r4, #0x04]
    lsl     r0, r0, #28
    lsr     r0, r0, #28
    cmp     r0, #1
    beq     Label_0x021A02E4
    cmp     r0, #5
    bne     Label_0x021A0318

Label_0x021A02E4:
    ldr     r1, [sp, #0x00]
    ldr     r0, =0x1D78
    add     r0, r1, r0
    ldr     r1, =0x0694
    bl      Battle::HEManager_PushState
    mov     r6, r0
    ldr     r0, [sp, #0x00]
    ldr     r1, [r4, #0x00]
    bl      Battle::ServerEvent_CheckSpecialPriority
    lsl     r0, r0, #29
    ldr     r2, [r4, #0x08]
    ldr     r1, =0xFFFF1FFF
    lsr     r0, r0, #16
    and     r1, r2
    orr     r0, r1
    str     r0, [r4, #0x08]
    ldr     r2, =0x0694
    ldr     r1, [sp, #0x00]
    ldr     r0, =0x1D78
    add     r2, r2, #3
    add     r0, r1, r0
    mov     r1, r6
    bl      Battle::HEManager_PopState

Label_0x021A0318:
    add     r0, r5, #1
    lsl     r0, r0, #24
    lsr     r5, r0, #24
    cmp     r5, r7
    bcc     Label_0x021A02D0

Label_0x021A0322:
    ldr     r0, [sp, #0x08]
    mov     r1, r7
    bl      Battle::SortActionSub

Label_0x021A032A:
    mov     r0, r7
    add     sp, #0x34
    pop     {r3-r7, pc}