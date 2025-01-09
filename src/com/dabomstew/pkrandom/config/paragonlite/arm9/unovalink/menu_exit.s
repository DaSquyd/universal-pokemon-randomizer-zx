    push    {r3-r6, lr}
    mov     r5, r0
    
    mov     r6, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.buttonCount)
    ldr     r0, [r5, r6]
    mov     r4, #0
    cmp     r0, #0
    bls     LoopEnd
    
LoopStart:
    mov     r0, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.buttons + UnovaLink_MenuParam_Button.msgWork)
    mov     r1, #UnovaLink_MenuParam_Button.SIZE
    mul     r1, r4
    ldr     r0, [r0, r1]
    bl      UnovaLink::Text_Exit
    
    ldr     r0, [r5, r6]
    add     r4, #1
    cmp     r4, r0
    bcc     LoopStart
    
LoopEnd:
    mov     r0, r5
    bl      ARM9::GFL_HeapFree
    pop     {r3-r6, pc}