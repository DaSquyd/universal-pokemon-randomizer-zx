    push    {r4, lr}
    mov     r4, r0
    
    mov     r0, #0
    str     r0, [r4, #UnovaLink_MenuWork.sequence]
    str     r0, [r4, #UnovaLink_MenuWork.isSelected]
    
    ldr     r0, [r4, #UnovaLink_MenuWork.cursor]
    mov     r1, #UnovaLink_MenuParam_Button.SIZE
    mul     r0, r1
    mov     r1, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.buttons + UnovaLink_MenuParam_Button.msgWork)
    add     r0, r1
    ldr     r0, [r4, r0]
    mov     r1, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.framePalette)
    ldr     r1, [r4, r1]
    add     r1, #1
    bl      UnovaLink::Text_ChangePalette
    
    mov     r0, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.frame)
    ldr     r0, [r4, r0]
    bl      ARM9::GFL_BGSysQueueScrLoad
    pop     {r4, pc}