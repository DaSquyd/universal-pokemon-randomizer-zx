#define S_Work 0x14
#define S_PaletteData 0x18

    push    {r4-r7, lr}
    sub     sp, #0x1C
    mov     r5, r0
    mov     r0, #233
    str     r1, [sp, #0x10]
    lsl     r0, r0, #2
    str     r0, [sp, #0x00]
    ldr     r0, [sp, #0x10]
    ldr     r3, =0x021C9330
    mov     r1, #236
    mov     r2, #1
    bl      ARM9::GFL_HeapAllocate
    str     r0, [sp, #S_Work]
    
    mov     r4, r5
    mov     r3, r0
    mov     r2, #9

Label_0x021C0E36:
    ldm     r4!, {r0-r1}
    stm     r3!, {r0-r1}
    sub     r2, r2, #1
    bne     Label_0x021C0E36
    
    ldr     r1, [r5, #0x1C] ; inParam->cursor
    ldr     r0, [sp, #S_Work]
    add     r2, sp, #S_PaletteData
    str     r1, [r0, #0x64] ; work->cursor
    ldr     r0, =0x0115
    ldr     r3, [sp, #0x10]
    mov     r1, #1
    bl      ARM9::GFL_G2DSysReadArcPaletteResource
    mov     r6, r0
    ldr     r0, [sp, #S_PaletteData]
    ldr     r1, [sp, #S_Work]
    ldr     r4, [r0, #0x0C]
    add     r1, #104
    mov     r0, r4
    mov     r2, #32
    blx     ARM9::memcpyx
    ldr     r1, [sp, #S_Work]
    mov     r0, r4
    add     r0, #32
    add     r1, #136
    mov     r2, #32
    blx     ARM9::memcpyx
    ldr     r1, [sp, #S_Work]
    add     r4, #64
    mov     r0, r4
    add     r1, #168
    mov     r2, #32
    blx     ARM9::memcpyx
    mov     r0, r6
    bl      ARM9::GFL_HeapFree
    ldr     r0, [r5, #0x18]
    mov     r7, #0
    cmp     r0, #0
    bls     Label_0x021C0EFE

Label_0x021C0E8C:
    lsl     r0, r7, #3
    add     r4, r5, r0
    ldr     r0, [sp, #S_Work]
    lsl     r1, r7, #2
    add     r6, r0, r1
    mov     r0, r4
    add     r0, #39
    ldrb    r0, [r0, #0x00]
    mov     r1, r4
    mov     r2, r4
    str     r0, [sp, #0x00]
    ldr     r0, [r5, #0x0C]
    mov     r3, r4
    lsl     r0, r0, #16
    lsr     r0, r0, #16
    str     r0, [sp, #0x04]
    ldr     r0, [r5, #0x14]
    add     r1, #36
    str     r0, [sp, #0x08]
    ldr     r0, [sp, #0x10]
    add     r2, #37
    str     r0, [sp, #0x0C]
    ldr     r0, [r5, #0x00]
    add     r3, #38
    lsl     r0, r0, #16
    ldrb    r1, [r1, #0x00]
    ldrb    r2, [r2, #0x00]
    ldrb    r3, [r3, #0x00]
    lsr     r0, r0, #16
    bl      UnovaLink::Text_Init
    ldr     r1, =0x39E1
    str     r0, [r6, #0x48]
    bl      UnovaLink::Text_SetColor
    ldr     r0, [r6, #0x48]
    bl      UnovaLink::Text_ClearCharacter
    ldr     r1, [r5, #0x08]
    ldr     r2, [r5, #0x0C]
    lsl     r1, r1, #16
    lsl     r2, r2, #24
    ldr     r0, [r6, #0x48]
    lsr     r1, r1, #16
    lsr     r2, r2, #24
    bl      UnovaLink::Text_WriteWindowFrame
    ldr     r0, [r6, #0x48]
    ldr     r1, [r5, #0x10]
    ldr     r2, [r4, #0x20]
    mov     r3, #0
    bl      UnovaLink::Text_Print
    ldr     r0, [r5, #0x18]
    add     r7, r7, #1
    cmp     r7, r0
    bcc     Label_0x021C0E8C

Label_0x021C0EFE:
    ldr     r0, [sp, #S_Work]
    ldr     r0, [r0, #0x64]
    lsl     r1, r0, #2
    ldr     r0, [sp, #S_Work]
    add     r0, r0, r1
    ldr     r1, [r5, #0x0C]
    ldr     r0, [r0, #0x48]
    add     r1, r1, #1
    lsl     r1, r1, #24
    lsr     r1, r1, #24
    bl      UnovaLink::Text_ChangePalette
    ldr     r0, [sp, #S_Work]
    add     sp, #0x1C
    pop     {r4-r7, pc}