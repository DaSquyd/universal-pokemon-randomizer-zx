#define S_ArgColumn 0x00
#define S_ArgStart 0x04
#define S_ArgEnd 0x08

    push    {r4-r6, lr}
    sub     sp, #0x0C
    mov     r5, r0
    
    mov     r0, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.buttonCount)
    ldr     r0, [r5, r0]
    mov     r4, #0
    cmp     r0, #0
    bls     Label_0x021C1140

Label_0x021C112E:
    mov     r0, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.buttons + UnovaLink_MenuParam_Button.msgWork)
    mov     r1, #UnovaLink_MenuParam_Button.SIZE
    mul     r1, r4
    add     r0, r1
    ldr     r0, [r5, r0]
    bl      UnovaLink::Text_Draw
    
    mov     r0, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.buttonCount)
    ldr     r0, [r5, r0]
    add     r4, r4, #1
    cmp     r4, r0
    bcc     Label_0x021C112E

Label_0x021C1140:
    mov     r6, r5
    mov     r4, #0
    add     r6, #200

Label_0x021C1148:
    lsl     r1, r4, #1
    mov     r0, r4
    str     r0, [sp, #S_ArgColumn]
    add     r0, r5, r1
    add     r0, #UnovaLink_MenuWork.backgroundChange2
    ldrh    r0, [r0]
    mov     r2, r5
    add     r2, #UnovaLink_MenuWork.backgroundCount
    str     r0, [sp, #S_ArgStart]
    add     r0, r5, r1
    add     r0, #UnovaLink_MenuWork.backgroundChange1
    ldrh    r0, [r0]
    add     r1, r6
    str     r0, [sp, #S_ArgEnd]
    mov     r3, #(UnovaLink_MenuWork.menuParam + UnovaLink_MenuParam.framePalette)
    ldr     r3, [r5, r3]
    ldrh    r2, [r2]
    add     r3, r3, #1
    mov     r0, #0x0F
    bl      UnovaLink::MainPaletteAnim
    add     r4, r4, #1
    cmp     r4, #16
    blt     Label_0x021C1148
    add     sp, #0x0C
    pop     {r4-r6, pc}