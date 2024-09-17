    push    {r4-r7, lr}
    sub     sp, #0x14
    mov     r5, r0
    mov     r4, r5
    mov     r0, r2
    add     r4, #156
    lsl     r7, r0, #3
    ldr     r0, [r4, r7]
    mov     r6, r1
    str     r2, [sp, #0x10]
    bl      ARM9::BmpWin_GetBitmap
    mov     r1, #0
    bl      ARM9::GFL_BitmapFill
    ldrb    r0, [r6, #Box2Main.levelAndEgg]
    lsl     r0, r0, #24
    lsr     r0, r0, #31
    bne     Label_0x021CE684
    mov     r0, r5
    add     r0, #140
    ldrh    r2, [r6, #Box2Main.ability]
    ldr     r0, [r0, #0x00]
    mov     r1, #0
    bl      ARM9::LoadAbilityNameToStrbuf
    mov     r0, #0
    str     r0, [sp, #0x00]
    str     r0, [sp, #0x04]
    mov     r0, r5
    add     r0, #128
    ldr     r0, [r0, #0x00]
    ldr     r1, [sp, #0x10]
    str     r0, [sp, #0x08]
    mov     r0, #17
    lsl     r0, r0, #6
    str     r0, [sp, #0x0C]
    mov     r0, r5
    add     r5, #136
    ldr     r2, [r5, #0x00]
    mov     r3, #97
    bl      Storage::Unk_21CE100
    b       Label_0x021CE6A6

Label_0x021CE684:
    mov     r0, #0
    str     r0, [sp, #0x00]
    str     r0, [sp, #0x04]
    mov     r0, r5
    add     r0, #128
    ldr     r0, [r0, #0x00]
    ldr     r1, [sp, #0x10]
    str     r0, [sp, #0x08]
    mov     r0, #17
    lsl     r0, r0, #6
    str     r0, [sp, #0x0C]
    mov     r0, r5
    add     r5, #136
    ldr     r2, [r5, #0x00]
    mov     r3, #106
    bl      Storage::Unk_21CE0A8

Label_0x021CE6A6:
    add     r0, r4, r7
    bl      Storage::Unk_21CE078
    add     sp, #0x14
    pop     {r4-r7, pc}