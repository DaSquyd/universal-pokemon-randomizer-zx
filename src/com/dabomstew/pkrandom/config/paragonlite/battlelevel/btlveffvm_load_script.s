    push    {r4-r7, lr}
    sub     sp, #0x0C
    str     r0, [sp, #0x00]
    str     r1, [sp, #0x04]
    str     r2, [sp, #0x08]
    mov     r6, r3
    ldr     r5, [sp, #0x20]
    bl      ARM9::VM_GetEnv
    mov     r7, #122
    mov     r4, r0
    lsl     r7, r7, #2
    mov     r0, #0
    str     r0, [r4, r7]
    mov     r0, #1
    bl      BattleLevel::BtlvEffectMain_RemoveAllTCB
    cmp     r5, #0
    beq     Label_0x021E0F5E
    add     r7, #96
    mov     r2, r5
    add     r3, r4, r7
    ldm     r2!, {r0-r1}
    stm     r3!, {r0-r1}
    ldm     r2!, {r0-r1}
    stm     r3!, {r0-r1}
    b       Label_0x021E0F6A

Label_0x021E0F5E:
    add     r7, #96
    mov     r0, #0
    add     r1, r4, r7
    mov     r2, #16
    blx     ARM9::sys_memset16

Label_0x021E0F6A:
    mov     r1, r4
    ldr     r0, [sp, #0x04]
    add     r1, #220
    str     r0, [r1, #0x00]
    mov     r1, r4
    ldr     r0, [sp, #0x08]
    add     r1, #224
    str     r0, [r1, #0x00]
    mov     r0, #145
    lsl     r0, r0, #2
    mov     r1, #1
    str     r1, [r4, r0]
    mov     r1, r0
    add     r1, #20
    strh    r6, [r4, r1]
    mov     r1, r0
    add     r1, #20
    mov     r2, r0
    ldrh    r1, [r4, r1]
    sub     r2, #19
    cmp     r1, r2
    bcc     Label_0x021E0FB8
    ldr     r2, =0xFD9F
    add     r2, r1, r2
    lsl     r2, r2, #16
    lsr     r2, r2, #16
    cmp     r2, #1
    bhi     Label_0x021E0FB8
    mov     r2, r0
    add     r2, #96
    ldr     r2, [r4, r2]
    cmp     r2, r1
    bne     Label_0x021E0FB8
    add     r0, #100
    ldr     r2, [r4, r0]
    ldr     r0, [sp, #0x04]
    cmp     r2, r0
    bne     Label_0x021E0FB8
    b       Label_0x021E11F6

Label_0x021E0FB8:
    mov     r2, #169
    lsl     r2, r2, #2
    str     r1, [r4, r2]
    add     r1, r2, #4
    ldr     r0, [sp, #0x04]
    sub     r2, #76
    str     r0, [r4, r1]
    ldrh    r0, [r4, r2]
    bl      BattleLevel::Unk_21E67D8
    cmp     r0, #0
    bne     Label_0x021E0FD6
    mov     r0, #0
    bl      BattleLevel::Unk_21E0364

Label_0x021E0FD6:
    mov     r0, #150
    lsl     r0, r0, #2
    ldrh    r1, [r4, r0]
    sub     r0, #39
    ldr     r3, =0x7FFF
    cmp     r1, r0
    bcs     Label_0x021E1056
    mov     r2, #150
    lsl     r2, r2, #2
    sub     r2, #32
    ldrh    r2, [r4, r2]
    mov     r0, #65
    mov     r1, r6
    and     r2, r3
    add     r3, r3, #1
    orr     r2, r3
    lsl     r2, r2, #16
    lsr     r2, r2, #16
    mov     r7, #65
    bl      ARM9::GFL_ArcSysReadHeapNew
    mov     r1, #150
    lsl     r1, r1, #2
    sub     r1, #112
    str     r0, [r4, r1]
    
MoveHideHP:
    mov     r0, #150
    lsl     r0, r0, #2
    ldrh    r0, [r4, r0]
    cmp     r0, #144
    beq     Label_0x021E1024
    cmp     r0, #119
    beq     Label_0x021E1024
    add     r7, #202
    cmp     r0, r7
    beq     Label_0x021E1024
    mov     r0, #0
    mov     r1, #2
    bl      BattleLevel::Unk_21DFBB0

Label_0x021E1024:
    mov     r0, #1
    mov     r1, #0
    bl      ARM9::GFL_BGSysSetBGEnabled
    mov     r0, #2
    mov     r1, #0
    bl      ARM9::GFL_BGSysSetBGEnabled
    mov     r0, #3
    mov     r1, #0
    bl      ARM9::GFL_BGSysSetBGEnabled
    mov     r2, #107
    ldr     r1, [r4, #0x00]
    mov     r0, #64
    bic     r1, r0
    str     r1, [r4, #0x00]
    mov     r1, #127
    mov     r0, r4
    lsl     r1, r1, #12
    lsl     r2, r2, #12
    mov     r3, #20
    bl      BattleLevel::Unk_21E6030
    b       Label_0x021E10A2

Label_0x021E1056:
    mov     r2, #150
    lsl     r2, r2, #2
    sub     r2, #32
    ldrh    r2, [r4, r2]
    mov     r1, #150
    lsl     r1, r1, #2
    and     r2, r3
    add     r3, r3, #1
    orr     r2, r3
    sub     r1, #39
    lsl     r2, r2, #16
    mov     r0, #66
    sub     r1, r6, r1
    lsr     r2, r2, #16
    bl      ARM9::GFL_ArcSysReadHeapNew
    mov     r1, #150
    lsl     r1, r1, #2
    sub     r1, #112
    str     r0, [r4, r1]
    
    ; NEW - Check if this is a high-id move
    mov     r0, #150
    lsl     r0, r0, #2
    ldrh    r1, [r4, r0]
    add     r0, #(BTLANM_HighMoveIdStart - 600)
    cmp     r1, r0
    bcs     MoveHideHP
    
    ldr     r1, [r4, #0x00]
    mov     r0, #64
    orr     r0, r1
    str     r0, [r4, #0x00]
    mov     r0, #150
    lsl     r0, r0, #2
    sub     r0, r0, #2
    cmp     r6, r0
    bcc     Label_0x021E10A2
    ldr     r0, =0x04000052
    ldrh    r1, [r0, #0x00]
    mov     r0, #31
    tst     r0, r1
    bne     Label_0x021E10A2
    mov     r0, #1
    mov     r1, #0
    bl      ARM9::GFL_BGSysSetBGEnabled

Label_0x021E10A2:
    mov     r6, #4
    cmp     r5, #0
    beq     Label_0x021E10C2
    mov     r0, #122
    lsl     r0, r0, #2
    ldr     r0, [r4, r0]
    ldrb    r1, [r0, #0x00]
    ldrb    r0, [r5, #0x01]
    cmp     r1, r0
    bhi     Label_0x021E10BA
    mov     r0, #0
    strb    r0, [r5, #0x01]

Label_0x021E10BA:
    ldrb    r1, [r5, #0x01]
    mov     r0, #56
    mul     r0, r1
    add     r6, r6, r0

Label_0x021E10C2:
    mov     r7, #0
    bl      BattleLevel::BtlvEffectMain_GetMcss
    mov     r1, r4
    add     r1, #220
    ldr     r1, [r1, #0x00]
    bl      BattleLevel::Mcss_HasDataFile
    cmp     r0, #0
    beq     Label_0x021E110E
    mov     r1, #150
    lsl     r1, r1, #2
    ldrh    r0, [r4, r1]
    cmp     r0, #248
    beq     Label_0x021E10E6
    sub     r1, #247
    cmp     r0, r1
    bne     Label_0x021E10EC

Label_0x021E10E6:
    ldrb    r1, [r5, #0x01]
    cmp     r1, #1
    beq     Label_0x021E110E

Label_0x021E10EC:
    cmp     r0, #144
    beq     Label_0x021E110E
    cmp     r0, #119
    beq     Label_0x021E110E
    ldr     r1, =0x010B
    cmp     r0, r1
    beq     Label_0x021E110E
    bl      BattleLevel::BtlvEffectMain_GetMcss
    mov     r1, r4
    add     r1, #220
    ldr     r1, [r1, #0x00]
    bl      BattleLevel::Unk_21E7D18
    mov     r1, #1
    mov     r7, r0
    and     r7, r1

Label_0x021E110E:
    mov     r0, #150
    lsl     r0, r0, #2
    ldrh    r1, [r4, r0]
    add     r0, #38
    cmp     r1, r0
    bne     Label_0x021E1124
    mov     r0, #1
    ldr     r1, [r4, #0x00]
    lsl     r0, r0, #18
    orr     r0, r1
    str     r0, [r4, #0x00]

Label_0x021E1124:
    mov     r0, #150
    lsl     r0, r0, #2
    ldrh    r1, [r4, r0]
    add     r0, #57
    cmp     r1, r0
    bne     Label_0x021E1138
    ldr     r1, [r4, #0x00]
    ldr     r0, =0xFFFBFFFF
    and     r0, r1
    str     r0, [r4, #0x00]

Label_0x021E1138:
    mov     r5, #150
    lsl     r5, r5, #2
    ldrh    r0, [r4, r5]
    mov     r1, r5
    add     r1, #38
    cmp     r0, r1
    bne     Label_0x021E1184
    cmp     r7, #0
    beq     Label_0x021E1184
    mov     r0, r5
    sub     r0, #112
    ldr     r1, [r4, r0]
    mov     r0, r5
    sub     r0, #108
    str     r1, [r4, r0]
    mov     r0, r5
    sub     r0, #104
    mov     r2, r5
    str     r6, [r4, r0]
    sub     r2, #32
    ldrh    r3, [r4, r2]
    ldr     r2, =0x7FFF
    mov     r0, #66
    and     r3, r2
    add     r2, r2, #1
    orr     r2, r3
    lsl     r2, r2, #16
    mov     r1, #51
    lsr     r2, r2, #16
    bl      ARM9::GFL_ArcSysReadHeapNew
    sub     r5, #112
    str     r0, [r4, r5]
    ldr     r1, [r4, #0x00]
    mov     r0, #64
    orr     r1, r0
    lsl     r0, r0, #11
    b       Label_0x021E11CA

Label_0x021E1184:
    ldr     r1, [r4, #0x00]
    lsl     r2, r1, #25
    lsr     r2, r2, #31
    bne     Label_0x021E11D0
    cmp     r0, #144
    beq     Label_0x021E11D0
    lsl     r0, r1, #14
    lsr     r0, r0, #31
    bne     Label_0x021E11D0
    cmp     r7, #0
    beq     Label_0x021E11D0
    mov     r5, #122
    lsl     r5, r5, #2
    ldr     r1, [r4, r5]
    add     r0, r5, #4
    str     r1, [r4, r0]
    mov     r0, r5
    add     r0, #8
    mov     r2, r5
    str     r6, [r4, r0]
    add     r2, #80
    ldrh    r3, [r4, r2]
    ldr     r2, =0x7FFF
    mov     r0, #66
    and     r3, r2
    add     r2, r2, #1
    orr     r2, r3
    lsl     r2, r2, #16
    mov     r1, #51
    lsr     r2, r2, #16
    bl      ARM9::GFL_ArcSysReadHeapNew
    str     r0, [r4, r5]
    ldr     r1, [r4, #0x00]
    mov     r0, #64

Label_0x021E11CA:
    orr     r0, r1
    str     r0, [r4, #0x00]
    mov     r6, #4

Label_0x021E11D0:
    mov     r2, #122
    lsl     r2, r2, #2
    ldr     r1, [r4, r2]
    mov     r0, #0
    str     r0, [r4, #0x04]
    ldr     r3, [r4, #0x00]
    ldr     r0, =0xFFFFEFFF
    and     r0, r3
    str     r0, [r4, #0x00]
    mov     r0, r2
    mov     r3, #8
    add     r0, #116
    str     r3, [r4, r0]
    ldr     r2, [r4, r2]
    ldr     r1, [r1, r6]
    ldr     r0, [sp, #0x00]
    add     r1, r2, r1
    bl      ARM9::VM_LoadScript

Label_0x021E11F6:
    add     sp, #0x0C
    pop     {r4-r7, pc}
    
    dcd     0xFD9F
    dcd     0x7FFF
    dcd     0x04000052
    dcd     0x010B
    dcd     0xFFFBFFFF
    dcd     0xFFFFEFFF