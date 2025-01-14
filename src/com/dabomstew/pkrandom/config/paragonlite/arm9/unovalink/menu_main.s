#define SoundEffect_Hover 1352
#define SoundEffect_Click 1356
#define SoundEffect_Cancel 1361
#define SoundEffect_Warn 1367

    push    {r3-r7, lr}
    mov     r4, r0
    
    mov     r0, #FALSE
    str     r0, [r4, #UnovaLink_MenuWork.isChanging]
    
    ldr     r0, [r4, #UnovaLink_MenuWork.sequence]
    cmp     r0, #0
    beq     Seq_Selecting
    cmp     r0, #1
    bne     CheckSeqEnd
    b       Seq_SelectedAnim

CheckSeqEnd:
    cmp     r0, #2
    bne     Return
    b       Seq_End

Return:
    pop     {r3-r7, pc}

Seq_Selecting:
    bl      ARM9::GFL_HIDGetReleasedKeys
    mov     r7, r0
    bl      ARM9::GFL_HIDGetNewPressKeys
    mov     r1, #BTN_Up
    mov     r6, #0
    tst     r1, r7
    beq     Label_0x021C0F56
    ldr     r0, =SoundEffect_Hover
    bl      ARM9::PlaySeqFullVol
    ldr     r0, [r4, #UnovaLink_MenuWork.cursor]
    sub     r0, r0, #1
    str     r0, [r4, #UnovaLink_MenuWork.cursor]
    bpl     Label_0x021C0F50
    ldr     r0, [r4, #(UnovaLink_MenuWork.cursor.param + UnovaLink_MenuParam.buttonCount)]
    sub     r0, r0, #1

Label_0x021C0F50:
    str     r0, [r4, #UnovaLink_MenuWork.cursor]

Label_0x021C0F52:
    mov     r5, #1
    b       Label_0x021C0FB0

Label_0x021C0F56:
    mov     r1, #BTN_Down
    tst     r1, r7
    beq     Label_0x021C0F72
    ldr     r0, =SoundEffect_Hover
    bl      ARM9::PlaySeqFullVol
    ldr     r0, [r4, #UnovaLink_MenuWork.cursor]
    mov     r1, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.buttonCount)
    ldr     r1, [r4, r1]
    add     r0, r0, #1
    str     r0, [r4, #UnovaLink_MenuWork.cursor]
    blx     ARM9::DivideModUnsigned
    str     r1, [r4, #UnovaLink_MenuWork.cursor]
    b       Label_0x021C0F52

Label_0x021C0F72:
    mov     r2, #1
    mov     r1, r0
    tst     r1, r2
    beq     Label_0x021C0F9A
    mov     r6, r2
    mov     r2, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.callback)
    ldr     r2, [r4, r2]
    cmp     r2, #0
    beq     Label_0x021C0F8E
    ldr     r0, [r4, #UnovaLink_MenuWork.cursor]
    mov     r1, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.workAddress)
    ldr     r1, [r4, r1]
    bx      r2
    cmp     r0, #0
    bne     Label_0x021C0F8E
    mov     r6, r5

Label_0x021C0F8E:
    cmp     r6, #0
    beq     Label_0x021C0F96
    ldr     r0, =SoundEffect_Click
    b       Label_0x021C0FAC

Label_0x021C0F96:
    ldr     r0, =SoundEffect_Warn
    b       Label_0x021C0FAC

Label_0x021C0F9A:
    mov     r1, #2
    tst     r0, r1
    beq     Label_0x021C0FB0
    mov     r0, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.buttonCount)
    ldr     r0, [r4, r0]
    mov     r6, r2
    sub     r0, r0, #1
    str     r0, [r4, #UnovaLink_MenuWork.cursor]
    ldr     r0, =SoundEffect_Cancel
    str     r2, [r4, #UnovaLink_MenuWork.isChanging]

Label_0x021C0FAC:
    bl      ARM9::PlaySeqFullVol

Label_0x021C0FB0:
    mov     r0, r4
    add     r0, #UnovaLink_MenuWork.backgroundCount
    ldrh    r0, [r0]
    mov     r1, #1
    lsl     r1, r1, #10
    add     r3, r0, r1
    lsl     r2, r1, #6
    cmp     r3, r2
    blt     Label_0x021C0FCA
    mov     r1, #63
    lsl     r1, r1, #10
    sub     r1, r0, r1
    b       Label_0x021C0FD2

Label_0x021C0FCA:
    mov     r0, r4
    add     r0, #UnovaLink_MenuWork.backgroundCount
    ldrh    r0, [r0]
    add     r1, r0, r1

Label_0x021C0FD2:
    mov     r0, r4
    add     r0, #UnovaLink_MenuWork.backgroundCount
    strh    r1, [r0]
    cmp     r5, #0
    beq     Label_0x021C101C
    mov     r0, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.buttonCount)
    ldr     r0, [r4, r0]
    mov     r5, #0
    cmp     r0, #0
    bls     Label_0x021C1006

Label_0x021C0FE4:
    ldr     r0, [r4, #UnovaLink_MenuWork.cursor]
    cmp     r5, r0
    mov     r0, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.framePalette)
    ldr     r0, [r4, r0]
    bne     Label_0x021C0FF0
    add     r0, r0, #1
    b       Label_0x021C0FF0

Label_0x021C0FF0:
    mov     r1, r0
    mov     r0, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.buttons + UnovaLink_MenuParam_Button.msgWork)
    mov     r2, #UnovaLink_MenuParam_Button.SIZE
    mul     r2, r5
    add     r0, r2
    ldr     r0, [r4, r0]
    bl      UnovaLink::Text_ChangePalette
    
    mov     r0, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.buttonCount)
    ldr     r0, [r4, r0]
    add     r5, r5, #1
    cmp     r5, r0
    bcc     Label_0x021C0FE4

Label_0x021C1006:
    mov     r0, #1
    str     r0, [r4, #UnovaLink_MenuWork.isChanging]
    mov     r0, r4
    mov     r1, #0
    add     r0, #UnovaLink_MenuWork.backgroundCount
    strh    r1, [r0]
    mov     r0, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.frame)
    ldr     r0, [r4, r0]
    bl      ARM9::GFL_BGSysQueueScrLoad

Label_0x021C101C:
    cmp     r6, #0
    beq     Label_0x021C10CE
    mov     r0, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.buttonCount)
    ldr     r0, [r4, r0]
    mov     r5, #0
    cmp     r0, #0
    bls     Label_0x021C1040

Label_0x021C1028:
    mov     r0, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.buttons + UnovaLink_MenuParam_Button.msgWork)
    mov     r1, #UnovaLink_MenuParam_Button.SIZE
    mul     r1, r5
    add     r0, r1
    ldr     r0, [r4, r0]
    mov     r1, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.framePalette)
    ldr     r1, [r4, r1]
    bl      UnovaLink::Text_ChangePalette
    
    mov     r0, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.buttonCount)
    ldr     r0, [r4, r0]
    add     r5, r5, #1
    cmp     r5, r0
    bcc     Label_0x021C1028

Label_0x021C1040:
    mov     r3, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.framePalette)
    ldr     r3, [r4, r3]
    mov     r0, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.frame)
    ldr     r0, [r4, r0]
    add     r3, r3, #1
    mov     r1, r4
    lsl     r3, r3, #21
    add     r1, #UnovaLink_MenuWork.backgroundChange1
    mov     r2, #32
    lsr     r3, r3, #16
    bl      ARM9::GFL_BGSysUploadStdPalette
    ldr     r0, [r4, #UnovaLink_MenuWork.sequence]
    mov     r1, #0
    add     r0, r0, #1
    str     r0, [r4, #UnovaLink_MenuWork.sequence]
    mov     r0, r4
    add     r0, #UnovaLink_MenuWork.backgroundCount
    add     r4, #UnovaLink_MenuWork.selectedAnim
    strh    r1, [r0]
    mov     r1, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.framePalette)
    strh    r1, [r4, r1]
    pop     {r3-r7, pc}

Seq_SelectedAnim:
    mov     r0, r4
    add     r0, #UnovaLink_MenuWork.selectedAnim
    ldrh    r0, [r0]
    lsr     r0, r0, #2
    lsr     r2, r0, #31
    lsl     r1, r0, #31
    sub     r1, r1, r2
    mov     r0, #31
    ror     r1, r0
    add     r0, r2, r1
    mov     r0, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.framePalette)
    ldr     r0, [r4, r0]
    beq     Label_0x021C1088
    add     r0, r0, #1
    b       Label_0x021C1088

Label_0x021C1088:
    mov     r1, r0
    mov     r0, #UnovaLink_MenuParam_Button.SIZE
    ldr     r2, [r4, #UnovaLink_MenuWork.cursor]
    mul     r0, r2
    mov     r2, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.buttons + UnovaLink_MenuParam_Button.msgWork)
    add     r0, r2
    ldr     r0, [r4, r0]
    bl      UnovaLink::Text_ChangePalette
    
    mov     r0, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.framePalette)
    ldr     r0, [r4, r0]
    bl      ARM9::GFL_BGSysQueueScrLoad
    
    mov     r0, r4
    mov     r1, #0
    add     r0, #UnovaLink_MenuWork.backgroundCount
    strh    r1, [r0]
    mov     r0, r4
    add     r0, #UnovaLink_MenuWork.selectedAnim
    ldrh    r2, [r0]
    mov     r0, r4
    add     r0, #UnovaLink_MenuWork.selectedAnim
    ldrh    r0, [r0]
    add     r1, r0, #1
    mov     r0, r4
    add     r0, #UnovaLink_MenuWork.selectedAnim
    strh    r1, [r0]
    cmp     r2, #16
    bls     Label_0x021C10CE
    ldr     r0, [r4, #UnovaLink_MenuWork.sequence]
    add     r0, r0, #1
    str     r0, [r4, #UnovaLink_MenuWork.sequence]
    pop     {r3-r7, pc}

Seq_End:
    mov     r0, #1
    str     r0, [r4, #UnovaLink_MenuWork.isSelected]

Label_0x021C10CE:
    pop     {r3-r7, pc}
    