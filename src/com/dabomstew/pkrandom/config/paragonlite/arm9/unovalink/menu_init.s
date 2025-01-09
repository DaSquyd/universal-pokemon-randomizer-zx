#define S_ArgLineNum 0x00

#define S_ArgHeight 0x00
#define S_ArgFramePalette 0x04
#define S_ArgFont 0x08
#define S_ArgHeapId 0x0C

#define S_HeapId 0x10
#define S_Work 0x14
#define S_PaletteData 0x18
#define S_ParamSize 0x1C

#define StackSize 0x20

#define TextColor_L 0x0E ; letter
#define TextColor_S 0x0F ; shadow
#define TextColor_B 0x01 ; background
#define TextColor ((TextColor_L & 0x1f) << 10) | ((TextColor_S & 0x1f) << 5) | (TextColor_B & 0x1f)

    push    {r4-r7, lr}
    sub     sp, #StackSize
    mov     r5, r0
    str     r1, [sp, #S_HeapId]
    
    ldr     r0, [r5, #UnovaLink_MenuParam.buttonCount]
    mov     r1, #(UnovaLink_MenuParam_Button.SIZE)
    mul     r0, r1
    mov     r1, #(UnovaLink_MenuParam.SIZE_WITHOUT_BUTTONS)
    add     r0, r1 ; r0 := size of menu param
    str     r0, [sp, #S_ParamSize]
    
    mov     r1, #(UnovaLink_MenuWork.SIZE_WITHOUT_PARAM) ; TODO: Restore this
    add     r1, r0 ; r1 := size of menu work ; TODO: Restore this
;    mov     r1, #0xEC ; TODO: Remove this temp value
    
    mov     r0, #233
    lsl     r0, r0, #2
    str     r0, [sp, #S_ArgLineNum]
    ldr     r0, [sp, #S_HeapId]
    mov     r2, #1
    ldr     r3, =UnovaLink::Data_aKeySystemUtilC
    bl      ARM9::GFL_HeapAllocate
    str     r0, [sp, #S_Work]
    
    mov     r3, r5
    mov     r2, r0
    ldr     r1, [sp, #S_ParamSize]
    lsr     r1, #2 ; as 32-bit sized values
    
    ;memcpy
CopyStart:
    ldm     r3!, {r0} ; read
    stm     r2!, {r0} ; write
    sub     r1, #1
    bne     CopyStart
    
    ldr     r1, [r5, #UnovaLink_MenuParam.cursor]
    ldr     r0, [sp, #S_Work]
    add     r2, sp, #S_PaletteData
    str     r1, [r0, #UnovaLink_MenuWork.cursor]
    ldr     r0, =277 ; arc id
    ldr     r3, [sp, #S_HeapId]
    mov     r1, #1 ; file id
    bl      ARM9::GFL_G2DSysReadArcPaletteResource
    mov     r6, r0
    
    ldr     r0, [sp, #S_PaletteData]
    ldr     r4, [r0, #0x0C] ; paletteData.rawData
    
    mov     r0, r4
    ldr     r1, [sp, #S_Work]
    add     r1, #UnovaLink_MenuWork.background
    mov     r2, #0x20
    blx     ARM9::memcpyx
    
    mov     r0, r4
    add     r0, #0x20
    ldr     r1, [sp, #S_Work]
    add     r1, #UnovaLink_MenuWork.backgroundChange1
    mov     r2, #0x20
    blx     ARM9::memcpyx
    
    add     r4, #0x40
    mov     r0, r4
    ldr     r1, [sp, #S_Work]
    add     r1, #UnovaLink_MenuWork.backgroundChange2
    mov     r2, #0x20
    blx     ARM9::memcpyx
    
    mov     r0, r6
    bl      ARM9::GFL_HeapFree
    
    ldr     r5, [sp, #S_Work]
    mov     r0, #UnovaLink_MenuWork.menuParam
    add     r5, r0 ; r5 := menu_param
    
    ldr     r0, [r5, #UnovaLink_MenuParam.buttonCount]
    mov     r7, #0
    cmp     r0, #0
    bls     ButtonLoop_End
    
ButtonLoop_Start:
    mov     r0, #UnovaLink_MenuParam.buttons
    add     r0, r5
    mov     r1, #UnovaLink_MenuParam_Button.SIZE
    mul     r1, r7
    add     r4, r0, r1 ; r4 := button
    
    ldrb    r0, [r4, #UnovaLink_MenuParam_Button.height]
    str     r0, [sp, #S_ArgHeight]                          ; arg 4
    ldr     r0, [r5, #UnovaLink_MenuParam.framePalette]
    str     r0, [sp, #S_ArgFramePalette]                    ; arg 5
    ldr     r0, [r5, #UnovaLink_MenuParam.font]
    str     r0, [sp, #S_ArgFont]                            ; arg 6
    ldr     r0, [sp, #S_HeapId]
    str     r0, [sp, #S_ArgHeapId]                          ; arg 7
    ldr     r0, [r5, #UnovaLink_MenuParam.frame]            ; arg 0
    ldrb    r1, [r4, #UnovaLink_MenuParam_Button.x]         ; arg 1
    ldrb    r2, [r4, #UnovaLink_MenuParam_Button.y]         ; arg 2
    ldrb    r3, [r4, #UnovaLink_MenuParam_Button.width]     ; arg 3
    
    bl      UnovaLink::Text_Init
    str     r0, [r4, #UnovaLink_MenuParam_Button.msgWork]
    ldr     r1, =TextColor
    bl      UnovaLink::Text_SetColor
    
    ldr     r0, [r4, #UnovaLink_MenuParam_Button.msgWork]
    bl      UnovaLink::Text_ClearCharacter
    
    ldr     r0, [r4, #UnovaLink_MenuParam_Button.msgWork]
    ldr     r1, [r5, #UnovaLink_MenuParam.frameGraphic]
    ldr     r2, [r5, #UnovaLink_MenuParam.framePalette]
    bl      UnovaLink::Text_WriteWindowFrame
    
    ldr     r0, [r4, #UnovaLink_MenuParam_Button.msgWork]
    ldr     r1, [r5, #UnovaLink_MenuParam.message]
    ldr     r2, [r4, #UnovaLink_MenuParam_Button.strId]
    mov     r3, #0 ; type: Queue
    bl      UnovaLink::Text_Print
    
    ldr     r0, [r5, #UnovaLink_MenuParam.buttonCount]
    add     r7, #1
    cmp     r7, r0
    bcc     ButtonLoop_Start
    
ButtonLoop_End:

    mov     r0, #UnovaLink_MenuParam.buttons
    add     r0, r5
    ldr     r1, [sp, #S_Work]
    ldr     r1, [r1, #UnovaLink_MenuWork.cursor]
    mov     r2, #UnovaLink_MenuParam_Button.SIZE
    mul     r1, r2
    add     r4, r0, r1 ; r4 := button
    
    ldr     r0, [r4, #UnovaLink_MenuParam_Button.msgWork]
    ldr     r1, [r5, #UnovaLink_MenuParam.framePalette]
    add     r1, #1
    bl      UnovaLink::Text_ChangePalette ; (work.param.buttons[work.cursor].msgWork, work.param.framePalette+1)
    
    ldr     r0, [sp, #S_Work]
    add     sp, #StackSize
    pop     {r4-r7, pc}
    