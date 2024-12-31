#define S_ArgLineNum 0x00

#define S_ArgHeight 0x00
#define S_ArgFontPalette 0x04
#define S_ArgFont 0x08
#define S_ArgHeapId 0x0C

#define S_HeapId 0x10
#define S_Work 0x14
#define S_PaletteData 0x18
#define S_ParamSize 0x1C

#define StackSize 0x20

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
    
    mov     r1, #(UnovaLink_MenuWork.SIZE_WITHOUT_PARAM)
    add     r1, r0 ; r1 := size of menu work
    
    mov     r0, #0
    str     r0, [sp, #S_ArgLineNum]
    ldr     r0, [sp, #S_HeapId]
    mov     r2, #1
    ldr     r3, =UnovaLink::Data_aKeySystemUtilC
    bl      ARM9::GFL_HeapAllocate
    str     r0, [sp, #S_Work]
    
    mov     r3, r5
    mov     r2, r0
    ldr     r1, [sp, #S_ParamSize]
    lsl     r1, #2 ; as 32-bit sized values
    
CopyStart:
    ldr     r3!, {r0} ; read
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
    ldr     r1, [sp, #S_Work]
    ldr     r4, #UnovaLink_MenuWork.background
    mov     r0, r4
    mov     r2, #0x20
    blx     ARM9::memcpyx
    
    ldr     r1, [sp, #S_Work]
    mov     r0, r4
    add     r0, #0x20
    add     r1, #UnovaLink_MenuWork.backgroundChange1
    mov     r2, #0x20
    blx     ARM9::memcpyx
    
    ldr     r1, [sp, #S_Work]
    add     r4, #0x40
    mov     r0, r4
    add     r1, #UnovaLink_MenuWork.backgroundChange2
    mov     r2, #0x20
    blx     ARM9::memcpyx
    
    mov     r0, r6
    bl      ARM9::GFL_HeapFree
    
    ldr     r0, [r5, #UnovaLink_MenuParam.buttonCount]
    mov     r7, #0
    cmp     r0, #0
    bls     ButtonLoop_End
    
ButtonLoop_Start:
    