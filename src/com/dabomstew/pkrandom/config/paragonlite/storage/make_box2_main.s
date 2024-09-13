    push    {r3-r7, lr}
    mov     r1, #169
    mov     r2, #0
    mov     r6, r0
    mov     r5, #0
    bl      ARM9::GetPokeBoxData
    cmp     r0, #0
    bne     Label_0x021BF074
    b       Label_0x021BF1D0

Label_0x021BF074:
    ldr     r0, =0x0FE6
    ldr     r3, =Storage::Box2_Main_C
    str     r0, [sp, #0x00]
    mov     r0, #76
    mov     r1, #28
    mov     r2, r5
    bl      ARM9::GFL_HeapAllocate
    mov     r4, r0
    str     r6, [r4, #0x00]
    mov     r0, r6
    mov     r1, #5
    mov     r2, r5
    bl      ARM9::GetPokeBoxData
    strh    r0, [r4, #0x04]
    mov     r0, r6
    mov     r1, #6
    mov     r2, r5
    bl      ARM9::GetPokeBoxData
    strh    r0, [r4, #0x06]
    mov     r0, r6
    mov     r1, r5
    mov     r2, r5
    bl      ARM9::GetPokeBoxData
    str     r0, [r4, #0x08]
    mov     r0, r6
    mov     r1, #174
    mov     r2, r5
    bl      ARM9::GetPokeBoxData
    strb    r0, [r4, #0x0C]
    mov     r0, r6
    mov     r1, #175
    mov     r2, r5
    bl      ARM9::GetPokeBoxData
    strb    r0, [r4, #0x0D]
    mov     r0, r6
    mov     r1, #10
    mov     r2, r5
    bl      ARM9::GetPokeBoxData
    strb    r0, [r4, #0x0E] ; Ability
    lsr     r1, r0 #8
    lsl     r1, #6
    mov     r0, r6
    bl      ARM9::GetPokeNature
    orr     r0, r1
    strb    r0, [r4, #0x0F]
    mov     r0, r6
    mov     r1, #11
    mov     r2, r5
    bl      ARM9::GetPokeBoxData
    strh    r0, [r4, #0x10]
    mov     r0, r6
    mov     r1, #158
    mov     r2, r5
    bl      ARM9::GetPokeBoxData
    ldrb    r1, [r4, #0x12]
    mov     r2, #127
    lsl     r0, r0, #24
    bic     r1, r2
    lsr     r2, r0, #24
    mov     r0, #127
    and     r0, r2
    orr     r0, r1
    strb    r0, [r4, #0x12]
    mov     r0, r6
    mov     r1, #76
    mov     r2, r5
    bl      ARM9::GetPokeBoxData
    lsl     r0, r0, #24
    lsr     r0, r0, #24
    lsl     r0, r0, #31
    ldrb    r1, [r4, #0x12]
    mov     r2, #128
    lsr     r0, r0, #24
    bic     r1, r2
    orr     r0, r1
    strb    r0, [r4, #0x12]
    ldrb    r0, [r4, #0x12]
    lsl     r0, r0, #24
    lsr     r0, r0, #31
    bne     Label_0x021BF13A
    mov     r0, r6
    bl      ARM9::GetGenderType
    ldrb    r2, [r4, #0x13]
    mov     r1, #15
    bic     r2, r1
    mov     r1, #15
    and     r0, r1
    orr     r0, r2
    strb    r0, [r4, #0x13]
    b       Label_0x021BF142

Label_0x021BF13A:
    ldrb    r1, [r4, #0x13]
    mov     r0, #15
    bic     r1, r0
    strb    r1, [r4, #0x13]

Label_0x021BF142:
    mov     r0, r6
    bl      ARM9::IsPokeShiny
    cmp     r0, #1
    ldrb    r1, [r4, #0x13]
    bne     Label_0x021BF156
    mov     r0, #32
    orr     r0, r1
    strb    r0, [r4, #0x13]
    b       Label_0x021BF15C

Label_0x021BF156:
    mov     r0, #32
    bic     r1, r0
    strb    r1, [r4, #0x13]

Label_0x021BF15C:
    mov     r0, r6
    bl      ARM9::DoesPokerusHaveDuration
    cmp     r0, #1
    bne     Label_0x021BF174
    ldrb    r1, [r4, #0x13]
    mov     r0, #192
    bic     r1, r0
    mov     r0, #64

Label_0x021BF16E:
    orr     r0, r1
    strb    r0, [r4, #0x13]
    b       Label_0x021BF18E

Label_0x021BF174:
    mov     r0, r6
    bl      ARM9::PokeHasPokerus
    cmp     r0, #1
    ldrb    r1, [r4, #0x13]
    bne     Label_0x021BF188
    mov     r0, #192
    bic     r1, r0
    mov     r0, #128
    b       Label_0x021BF16E

Label_0x021BF188:
    mov     r0, #192
    bic     r1, r0
    strb    r1, [r4, #0x13]

Label_0x021BF18E:
    ldrh    r0, [r4, #0x04]
    cmp     r0, #29
    beq     Label_0x021BF1AA
    cmp     r0, #32
    beq     Label_0x021BF1AA
    ldrb    r0, [r4, #0x12]
    lsl     r0, r0, #24
    lsr     r0, r0, #31
    bne     Label_0x021BF1AA
    ldrb    r1, [r4, #0x13]
    mov     r0, #16
    orr     r0, r1
    strb    r0, [r4, #0x13]
    b       Label_0x021BF1B2

Label_0x021BF1AA:
    ldrb    r1, [r4, #0x13]
    mov     r0, #16
    bic     r1, r0
    strb    r1, [r4, #0x13]

Label_0x021BF1B2:
    mov     r5, #0
    mov     r7, r5

Label_0x021BF1B6:
    mov     r1, r5
    mov     r0, r6
    add     r1, #54
    mov     r2, r7
    bl      ARM9::GetPokeBoxData
    lsl     r1, r5, #1
    add     r1, r4, r1
    add     r5, r5, #1
    strh    r0, [r1, #0x14]
    cmp     r5, #4
    bcc     Label_0x021BF1B6
    b       Label_0x021BF1D2

Label_0x021BF1D0:
    mov     r4, r5

Label_0x021BF1D2:
    mov     r0, r4
    pop     {r3-r7, pc}