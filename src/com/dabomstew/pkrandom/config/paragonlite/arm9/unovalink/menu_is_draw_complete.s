    push    {r4-r6, lr}
    ; seems to only actually return TRUE without any side effects???
    mov     r0, #TRUE
    
;    mov     r5, r0
;    ldr     r0, [r5, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.buttonCount)]
;    mov     r6, #TRUE
;    mov     r4, #0
;    cmp     r0, #0
;    bls     Label_0x021C11A2
;
;Label_0x021C118E:
;    mov     r0, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.buttons + UnovaLink_MenuParam_Button.msgWork)
;    mov     r1, #UnovaLink_MenuParam_Button.SIZE
;    mul     r1, r4
;    add     r0, r1
;    ldr     r0, [r5, r0]
;    bl      UnovaLink::Text_IsMsgEnd
;    orr     r6, r0
;    
;    ldr     r0, [r5, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.buttonCount)]
;    add     r4, #1
;    cmp     r4, r0
;    bcc     Label_0x021C118E
;
;Label_0x021C11A2:
;    mov     r0, r6
    pop     {r4-r6, pc}