    push    {r3-r7, lr}
    sub     sp, #0x10
    mov     r7, r0
    str     r1, [sp, #0x00]
    str     r2, [sp, #0x04]
    ldr     r1, [r7, #0x00]
    mov     r2, #0
    mov     r4, #0
    bl      ARM9::GetPokeBlockShuffle
    mov     r2, #1
    mov     r5, r0
    str     r2, [sp, #0x08]
    ldr     r1, [r7, #0x00]
    mov     r0, r7
    mov     r2, #1
    bl      ARM9::GetPokeBlockShuffle
    mov     r6, r0
    ldr     r1, [r7, #0x00]
    mov     r0, r7
    mov     r2, #2
    bl      ARM9::GetPokeBlockShuffle
    str     r0, [sp, #0x0C]
    ldr     r1, [r7, #0x00]
    mov     r0, r7
    mov     r2, #3
    bl      ARM9::GetPokeBlockShuffle
    ldr     r2, [sp, #0x00]
    mov     r1, r0
    cmp     r2, #179
    bls     Label_0x0201DF3A
    b       Label_0x0201E0AE

Label_0x0201DF3A:
    #SWITCH r2
    #CASE Label_0x0201E0B2
    #CASE Label_0x0201E0B6
    #CASE Label_0x0201E0BE
    #CASE Label_0x0201E0C2
    #CASE Label_0x0201E0C8
    #CASE Label_0x0201E110
    #CASE Label_0x0201E11E
    #CASE Label_0x0201E128
    #CASE Label_0x0201E12C
    #CASE Label_0x0201E130
    #CASE Ability
    #CASE Markings
    #CASE Label_0x0201E13C
    #CASE Label_0x0201E140
    #CASE Label_0x0201E144
    #CASE Label_0x0201E148
    #CASE Label_0x0201E14C
    #CASE Label_0x0201E150
    #CASE Label_0x0201E154
    #CASE Label_0x0201E158
    #CASE Label_0x0201E15C
    #CASE Label_0x0201E160
    #CASE Label_0x0201E164
    #CASE Label_0x0201E168
    #CASE Label_0x0201E16C
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E170
    #CASE Label_0x0201E198
    #CASE Label_0x0201E198
    #CASE Label_0x0201E198
    #CASE Label_0x0201E198
    #CASE Label_0x0201E1A4
    #CASE Label_0x0201E1A4
    #CASE Label_0x0201E1A4
    #CASE Label_0x0201E1A4
    #CASE Label_0x0201E1B0
    #CASE Label_0x0201E1B0
    #CASE Label_0x0201E1B0
    #CASE Label_0x0201E1B0
    #CASE Label_0x0201E1BC
    #CASE Label_0x0201E1BC
    #CASE Label_0x0201E1BC
    #CASE Label_0x0201E1BC
    #CASE Label_0x0201E1D8
    #CASE Label_0x0201E1E0
    #CASE Label_0x0201E1E6
    #CASE Label_0x0201E1EC
    #CASE Label_0x0201E1F2
    #CASE Label_0x0201E1F8
    #CASE Label_0x0201E1FE
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E212
    #CASE Label_0x0201E23A
    #CASE Label_0x0201E23E
    #CASE Label_0x0201E26E
    #CASE Label_0x0201E274
    #CASE Label_0x0201E278
    #CASE Label_0x0201E282
    #CASE Label_0x0201E286
    #CASE Label_0x0201E2A6
    #CASE Label_0x0201E20E
    #CASE Label_0x0201E2CA
    #CASE Label_0x0201E2D0
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E2D6
    #CASE Label_0x0201E302
    #CASE Label_0x0201E306
    #CASE Label_0x0201E30C
    #CASE Label_0x0201E310
    #CASE Label_0x0201E314
    #CASE Label_0x0201E318
    #CASE Label_0x0201E31C
    #CASE Label_0x0201E320
    #CASE Label_0x0201E324
    #CASE Label_0x0201E328
    #CASE Label_0x0201E32C
    #CASE Label_0x0201E330
    #CASE Label_0x0201E334
    #CASE Label_0x0201E33C
    #CASE Label_0x0201E342
    #CASE Label_0x0201E346
    #CASE Label_0x0201E0AE
    #CASE Label_0x0201E100
    #CASE Label_0x0201E0AE
    #CASE Label_0x0201E0AE
    #CASE Label_0x0201E0AE
    #CASE Label_0x0201E0AE
    #CASE Label_0x0201E0AE
    #CASE Label_0x0201E0AE
    #CASE Label_0x0201E0AE
    #CASE Label_0x0201E0AE
    #CASE Label_0x0201E0AE
    #CASE Label_0x0201E0AE
    #CASE Label_0x0201E0CC
    #CASE Label_0x0201E0DA
    #CASE Label_0x0201E0E4
    #CASE Label_0x0201E34A
    #CASE Label_0x0201E37C
    #CASE Label_0x0201E392
    #CASE Label_0x0201E392
    #CASE Label_0x0201E0AE
    #CASE Label_0x0201E0AE
    #CASE Label_0x0201E27C
    #CASE Label_0x0201E3BA

Label_0x0201E0AE:
    mov     r4, #0
    b       Label_0x0201E3BC

Label_0x0201E0B2:
    ldr     r4, [r7, #0x00]
    b       Label_0x0201E3BC

Label_0x0201E0B6:
    ldrh    r0, [r7, #0x04]

Label_0x0201E0B8:
    lsl     r0, r0, #31

Label_0x0201E0BA:
    lsr     r4, r0, #31
    b       Label_0x0201E3BC

Label_0x0201E0BE:
    ldrh    r0, [r7, #0x04]
    b       Label_0x0201E27E

Label_0x0201E0C2:
    ldrh    r0, [r7, #0x04]
    lsl     r0, r0, #29
    b       Label_0x0201E0BA

Label_0x0201E0C8:
    ldrh    r4, [r7, #0x06]
    b       Label_0x0201E3BC

Label_0x0201E0CC:
    ldrh    r0, [r5, #0x00]
    cmp     r0, #0
    bne     Label_0x0201E0D6
    mov     r0, #0
    str     r0, [sp, #0x08]

Label_0x0201E0D6:
    ldr     r4, [sp, #0x08]
    b       Label_0x0201E3BC

Label_0x0201E0DA:
    ldrh    r0, [r7, #0x04]
    lsl     r0, r0, #29
    lsr     r4, r0, #31
    bne     Label_0x0201E0FA
    b       Label_0x0201E208

Label_0x0201E0E4:
    ldrh    r4, [r5, #0x00]
    cmp     r4, #0
    beq     Label_0x0201E0FA
    ldr     r0, [r6, #0x10]
    lsl     r0, r0, #1
    lsr     r0, r0, #31
    bne     Label_0x0201E0FC
    ldrh    r0, [r7, #0x04]
    lsl     r0, r0, #29
    lsr     r0, r0, #31
    bne     Label_0x0201E0FC

Label_0x0201E0FA:
    b       Label_0x0201E3BC

Label_0x0201E0FC:
    ldr     r4, =0x028A
    b       Label_0x0201E3BC

Label_0x0201E100:
    ldrb    r1, [r6, #0x18]
    ldrh    r0, [r5, #0x00]
    ldr     r2, [r5, #0x08]
    lsl     r1, r1, #24
    lsr     r1, r1, #27
    bl      ARM9::GetLevelFromExp
    b       Label_0x0201E1D4

Label_0x0201E110:
    ldrh    r0, [r7, #0x04]
    lsl     r0, r0, #29
    lsr     r0, r0, #31
    beq     Label_0x0201E11A
    b       Label_0x0201E0FC

Label_0x0201E11A:
    ldrh    r4, [r5, #0x00]
    b       Label_0x0201E3BC

Label_0x0201E11E:
    ldrh    r4, [r5, #0x02]
    ldr     r0, =0x027E
    cmp     r4, r0
    bls     Label_0x0201E206
    b       Label_0x0201E0AE

Label_0x0201E128:
    ldr     r4, [r5, #0x04]
    b       Label_0x0201E3BC

Label_0x0201E12C:
    ldr     r4, [r5, #0x08]
    b       Label_0x0201E3BC

Label_0x0201E130:
    ldrb    r4, [r5, #0x0C]
    b       Label_0x0201E3BC

Ability:
    ldrb    r4, [r5, #0x0D]
    ldrb    r0, [r5, #0x0E]
    lsr     r0, #6
    lsl     r0, #8
    orr     r4, r0
    b       Label_0x0201E3BC

Markings:
    ldrb    r4, [r5, #0x0E]
    lsl     r4, #26
    lsr     r4, #26
    b       Label_0x0201E3BC

Label_0x0201E13C:
    ldrb    r4, [r5, #0x0F]
    b       Label_0x0201E3BC

Label_0x0201E140:
    ldrb    r4, [r5, #0x10]
    b       Label_0x0201E3BC

Label_0x0201E144:
    ldrb    r4, [r5, #0x11]
    b       Label_0x0201E3BC

Label_0x0201E148:
    ldrb    r4, [r5, #0x12]
    b       Label_0x0201E3BC

Label_0x0201E14C:
    ldrb    r4, [r5, #0x13]
    b       Label_0x0201E3BC

Label_0x0201E150:
    ldrb    r4, [r5, #0x14]
    b       Label_0x0201E3BC

Label_0x0201E154:
    ldrb    r4, [r5, #0x15]
    b       Label_0x0201E3BC

Label_0x0201E158:
    ldrb    r4, [r5, #0x16]
    b       Label_0x0201E3BC

Label_0x0201E15C:
    ldrb    r4, [r5, #0x17]
    b       Label_0x0201E3BC

Label_0x0201E160:
    ldrb    r4, [r5, #0x18]
    b       Label_0x0201E3BC

Label_0x0201E164:
    ldrb    r4, [r5, #0x19]
    b       Label_0x0201E3BC

Label_0x0201E168:
    ldrb    r4, [r5, #0x1A]
    b       Label_0x0201E3BC

Label_0x0201E16C:
    ldrb    r4, [r5, #0x1B]
    b       Label_0x0201E3BC

Label_0x0201E170:
    ldr     r2, [sp, #0x00]
    ldr     r0, [sp, #0x08]
    sub     r2, #25
    mov     r1, #0
    str     r2, [sp, #0x00]
    bl      Unk_208D65A
    ldr     r3, [r5, #0x1C]
    mov     r2, #0
    and     r2, r1
    and     r3, r0
    mov     r1, #0
    mov     r0, #0
    eor     r1, r2
    eor     r0, r3
    orr     r0, r1
    bne     Label_0x0201E196
    mov     r0, #0
    str     r0, [sp, #0x08]

Label_0x0201E196:
    b       Label_0x0201E0D6

Label_0x0201E198:
    ldr     r0, [sp, #0x00]
    sub     r0, #54
    str     r0, [sp, #0x00]
    lsl     r0, r0, #1
    ldrh    r4, [r6, r0]
    b       Label_0x0201E3BC

Label_0x0201E1A4:
    ldr     r0, [sp, #0x00]
    sub     r0, #58
    str     r0, [sp, #0x00]
    add     r0, r6, r0
    ldrb    r4, [r0, #0x08]
    b       Label_0x0201E3BC

Label_0x0201E1B0:
    ldr     r0, [sp, #0x00]
    sub     r0, #62
    str     r0, [sp, #0x00]
    add     r0, r6, r0
    ldrb    r4, [r0, #0x0C]
    b       Label_0x0201E3BC

Label_0x0201E1BC:
    ldr     r0, [sp, #0x00]
    sub     r0, #66
    str     r0, [sp, #0x00]
    lsl     r0, r0, #1
    ldrh    r0, [r6, r0]
    cmp     r0, #0
    beq     Label_0x0201E206
    ldr     r1, [sp, #0x00]
    add     r1, r6, r1
    ldrb    r1, [r1, #0x0C]
    bl      ARM9::GetMoveMaxPP

Label_0x0201E1D4:
    mov     r4, r0
    b       Label_0x0201E3BC

Label_0x0201E1D8:
    ldr     r0, [r6, #0x10]
    lsl     r0, r0, #27

Label_0x0201E1DC:
    lsr     r4, r0, #27
    b       Label_0x0201E3BC

Label_0x0201E1E0:
    ldr     r0, [r6, #0x10]
    lsl     r0, r0, #22
    b       Label_0x0201E1DC

Label_0x0201E1E6:
    ldr     r0, [r6, #0x10]
    lsl     r0, r0, #17
    b       Label_0x0201E1DC

Label_0x0201E1EC:
    ldr     r0, [r6, #0x10]
    lsl     r0, r0, #12
    b       Label_0x0201E1DC

Label_0x0201E1F2:
    ldr     r0, [r6, #0x10]
    lsl     r0, r0, #7
    b       Label_0x0201E1DC

Label_0x0201E1F8:
    ldr     r0, [r6, #0x10]
    lsl     r0, r0, #2
    b       Label_0x0201E1DC

Label_0x0201E1FE:
    ldrh    r0, [r7, #0x04]
    lsl     r0, r0, #29
    lsr     r4, r0, #31
    beq     Label_0x0201E208

Label_0x0201E206:
    b       Label_0x0201E3BC

Label_0x0201E208:
    ldr     r0, [r6, #0x10]
    lsl     r0, r0, #1
    b       Label_0x0201E0BA

Label_0x0201E20E:
    ldr     r0, [r6, #0x10]
    b       Label_0x0201E0BA

Label_0x0201E212:
    ldr     r2, [sp, #0x00]
    ldr     r0, [sp, #0x08]
    sub     r2, #77
    mov     r1, #0
    str     r2, [sp, #0x00]
    bl      ARM9::LeftShift64
    ldr     r3, [r6, #0x14]
    mov     r2, #0
    and     r2, r1
    and     r3, r0
    mov     r1, #0
    mov     r0, #0
    eor     r1, r2
    eor     r0, r3
    orr     r0, r1
    bne     Label_0x0201E238
    mov     r0, #0
    str     r0, [sp, #0x08]

Label_0x0201E238:
    b       Label_0x0201E0D6

Label_0x0201E23A:
    ldrb    r0, [r6, #0x18]
    b       Label_0x0201E0B8

Label_0x0201E23E:
    ldrb    r1, [r6, #0x18]
    ldrh    r0, [r5, #0x00]
    ldr     r2, [r7, #0x00]
    lsl     r1, r1, #24
    lsr     r1, r1, #27
    bl      ARM9::ReduceGenderType
    mov     r4, r0
    ldrb    r0, [r6, #0x18]
    mov     r1, #6
    bic     r0, r1
    lsl     r1, r4, #30
    lsr     r1, r1, #29
    orr     r0, r1
    strb    r0, [r6, #0x18]
    mov     r0, r7
    add     r0, #8
    mov     r1, #128
    bl      ARM9::GeneratePokeChecksum
    strh    r0, [r7, #0x06]
    b       Label_0x0201E3BC

Label_0x0201E26E:
    ldrb    r0, [r6, #0x18]
    lsl     r0, r0, #24
    b       Label_0x0201E1DC

Label_0x0201E274:
    ldrb    r4, [r6, #0x19]
    b       Label_0x0201E3BC

Label_0x0201E278:
    ldrh    r0, [r6, #0x1A]
    b       Label_0x0201E0B8

Label_0x0201E27C:
    ldrh    r0, [r6, #0x1A]

Label_0x0201E27E:
    lsl     r0, r0, #30
    b       Label_0x0201E0BA

Label_0x0201E282:
    ldr     r4, [r6, #0x1C]
    b       Label_0x0201E3BC

Label_0x0201E286:
    ldrh    r0, [r7, #0x04]
    lsl     r0, r0, #29
    lsr     r0, r0, #31
    beq     Label_0x0201E29C
    ldr     r0, =0x0209A474
    ldr     r1, =0x028B
    ldr     r0, [r0, #0x00]
    ldr     r2, [sp, #0x04]
    bl      ARM9::GetTextFillStrBuf
    b       Label_0x0201E3BC

Label_0x0201E29C:
    ldr     r0, [sp, #0x04]
    ldr     r1, [sp, #0x0C]

Label_0x0201E2A0:
    bl      ARM9::WCharsCopy
    b       Label_0x0201E3BC

Label_0x0201E2A6:
    ldrh    r0, [r7, #0x04]
    lsl     r0, r0, #29
    lsr     r0, r0, #31
    beq     Label_0x0201E2BE
    ldr     r0, =0x0209A474
    ldr     r1, =0x028B
    ldr     r0, [r0, #0x00]
    ldr     r2, [sp, #0x04]
    mov     r3, #11
    bl      ARM9::FillStrBufFromFile
    b       Label_0x0201E3BC

Label_0x0201E2BE:
    ldr     r0, [sp, #0x0C]
    ldr     r1, [sp, #0x04]
    mov     r2, #11

Label_0x0201E2C4:
    bl      ARM9::WCharsNCopy
    b       Label_0x0201E3BC

Label_0x0201E2CA:
    ldr     r0, [sp, #0x0C]
    ldrb    r4, [r0, #0x16]
    b       Label_0x0201E3BC

Label_0x0201E2D0:
    ldr     r0, [sp, #0x0C]
    ldrb    r4, [r0, #0x17]
    b       Label_0x0201E3BC

Label_0x0201E2D6:
    ldr     r2, [sp, #0x00]
    ldr     r0, [sp, #0x08]
    sub     r2, #120
    mov     r1, #0
    str     r2, [sp, #0x00]
    bl      ARM9::LeftShift64
    ldr     r2, [sp, #0x0C]
    ldr     r4, [r2, #0x18]
    ldr     r2, [r2, #0x1C]
    mov     r3, r4
    and     r2, r1
    and     r3, r0
    mov     r1, #0
    mov     r0, #0
    eor     r1, r2
    eor     r0, r3
    orr     r0, r1
    bne     Label_0x0201E300
    mov     r0, #0
    str     r0, [sp, #0x08]

Label_0x0201E300:
    b       Label_0x0201E0D6

Label_0x0201E302:
    ldr     r0, [sp, #0x04]
    b       Label_0x0201E2A0

Label_0x0201E306:
    ldr     r1, [sp, #0x04]
    mov     r2, #8
    b       Label_0x0201E2C4

Label_0x0201E30C:
    ldrb    r4, [r1, #0x10]
    b       Label_0x0201E3BC

Label_0x0201E310:
    ldrb    r4, [r1, #0x11]
    b       Label_0x0201E3BC

Label_0x0201E314:
    ldrb    r4, [r1, #0x12]
    b       Label_0x0201E3BC

Label_0x0201E318:
    ldrb    r4, [r1, #0x13]
    b       Label_0x0201E3BC

Label_0x0201E31C:
    ldrb    r4, [r1, #0x14]
    b       Label_0x0201E3BC

Label_0x0201E320:
    ldrb    r4, [r1, #0x15]
    b       Label_0x0201E3BC

Label_0x0201E324:
    ldrh    r4, [r1, #0x16]
    b       Label_0x0201E3BC

Label_0x0201E328:
    ldrh    r4, [r1, #0x18]
    b       Label_0x0201E3BC

Label_0x0201E32C:
    ldrb    r4, [r1, #0x1A]
    b       Label_0x0201E3BC

Label_0x0201E330:
    ldrb    r4, [r1, #0x1B]
    b       Label_0x0201E3BC

Label_0x0201E334:
    ldrb    r0, [r1, #0x1C]
    lsl     r0, r0, #25
    lsr     r4, r0, #25
    b       Label_0x0201E3BC

Label_0x0201E33C:
    ldrb    r0, [r1, #0x1C]
    lsl     r0, r0, #24
    b       Label_0x0201E0BA

Label_0x0201E342:
    ldrb    r4, [r1, #0x1D]
    b       Label_0x0201E3BC

Label_0x0201E346:
    ldrb    r4, [r1, #0x1E]
    b       Label_0x0201E3BC

Label_0x0201E34A:
    ldr     r4, [r6, #0x10]
    lsl     r4, #2
    lsr     r4, #2
    b       Label_0x0201E3BC

Label_0x0201E37C:
    ldrh    r0, [r5, #0x00]
    cmp     r0, #29
    beq     Label_0x0201E386
    cmp     r0, #32
    bne     Label_0x0201E38E

Label_0x0201E386:
    ldr     r0, [r6, #0x10]
    lsr     r0, r0, #31
    bne     Label_0x0201E38E
    b       Label_0x0201E0AE

Label_0x0201E38E:
    mov     r4, #1
    b       Label_0x0201E3BC

Label_0x0201E392:
    ldrh    r0, [r5, #0x00]
    ldr     r1, =0x01ED
    cmp     r0, r1
    bne     Label_0x0201E3A8
    ldrb    r1, [r5, #0x0D]
    cmp     r1, #121
    bne     Label_0x0201E3A8
    ldrh    r0, [r5, #0x02]
    bl      ARM9::GetTypeForPlate
    b       Label_0x0201E1D4

Label_0x0201E3A8:
    ldrb    r1, [r6, #0x18]
    ldr     r2, [sp, #0x00]
    lsl     r1, r1, #24
    sub     r2, #168
    lsr     r1, r1, #27
    str     r2, [sp, #0x00]
    bl      ARM9::GetPersonalField
    b       Label_0x0201E1D4

Label_0x0201E3BA:
    ldrb    r4, [r1, #0x1F]

Label_0x0201E3BC:
    mov     r0, r4
    add     sp, #0x10
    pop     {r3-r7, pc}
    
    dcd     0x028A
    dcd     0x027E
    dcd     0x0209A474
    dcd     0x028B
    dcd     0x01ED