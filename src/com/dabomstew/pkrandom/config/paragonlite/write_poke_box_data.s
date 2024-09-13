    push    {r3-r7, lr}
    sub     sp, #0x08
    mov     r7, r0
    str     r1, [sp, #0x00]
    mov     r4, r2
    ldr     r1, [r7, #0x00]
    mov     r2, #0
    bl      ARM9::GetPokeBlockShuffle
    mov     r6, r0
    ldr     r1, [r7, #0x00]
    mov     r0, r7
    mov     r2, #1
    bl      ARM9::GetPokeBlockShuffle
    mov     r5, r0
    ldr     r1, [r7, #0x00]
    mov     r0, r7
    mov     r2, #2
    bl      ARM9::GetPokeBlockShuffle
    str     r0, [sp, #0x04]
    ldr     r1, [r7, #0x00]
    mov     r0, r7
    mov     r2, #3
    bl      ARM9::GetPokeBlockShuffle
    mov     r1, r0
    ldr     r0, [sp, #0x00]
    cmp     r0, #179
    bls     Label_0x0201E498
    b       Label_0x0201EB28

Label_0x0201E498:
    #SWITCH r0
    #CASE Label_0x0201E60C
    #CASE Label_0x0201E612
    #CASE Label_0x0201E628
    #CASE Label_0x0201E63E
    #CASE Label_0x0201E654
    #CASE Label_0x0201E65A
    #CASE Label_0x0201E660
    #CASE Label_0x0201E66E
    #CASE Label_0x0201E674
    #CASE Label_0x0201E67A
    #CASE Ability
    #CASE Markings
    #CASE Label_0x0201E68C
    #CASE Label_0x0201E692
    #CASE Label_0x0201E6C0
    #CASE Label_0x0201E6EE
    #CASE Label_0x0201E71C
    #CASE Label_0x0201E74A
    #CASE Label_0x0201E778
    #CASE Label_0x0201E7A6
    #CASE Label_0x0201E7AC
    #CASE Label_0x0201E7B2
    #CASE Label_0x0201E7B8
    #CASE Label_0x0201E7BE
    #CASE Label_0x0201E7C4
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7CA
    #CASE Label_0x0201E7F2
    #CASE Label_0x0201E7F2
    #CASE Label_0x0201E7F2
    #CASE Label_0x0201E7F2
    #CASE Label_0x0201E800
    #CASE Label_0x0201E800
    #CASE Label_0x0201E800
    #CASE Label_0x0201E800
    #CASE Label_0x0201E80E
    #CASE Label_0x0201E80E
    #CASE Label_0x0201E80E
    #CASE Label_0x0201E80E
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EB28
    #CASE Label_0x0201E81C
    #CASE Label_0x0201E82E
    #CASE Label_0x0201E840
    #CASE Label_0x0201E852
    #CASE Label_0x0201E864
    #CASE Label_0x0201E876
    #CASE Label_0x0201E888
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E89A
    #CASE Label_0x0201E8C2
    #CASE Label_0x0201E8D8
    #CASE Label_0x0201E8F8
    #CASE Label_0x0201E90E
    #CASE Label_0x0201E914
    #CASE Label_0x0201E940
    #CASE Label_0x0201E946
    #CASE Label_0x0201E966
    #CASE Label_0x0201EB28
    #CASE Label_0x0201E9C8
    #CASE Label_0x0201E9D0
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201E9D8
    #CASE Label_0x0201EA26
    #CASE Label_0x0201EA32
    #CASE Label_0x0201EA3E
    #CASE Label_0x0201EA44
    #CASE Label_0x0201EA4A
    #CASE Label_0x0201EA50
    #CASE Label_0x0201EA56
    #CASE Label_0x0201EA5C
    #CASE Label_0x0201EA62
    #CASE Label_0x0201EA68
    #CASE Label_0x0201EA6E
    #CASE Label_0x0201EA74
    #CASE Label_0x0201EA7A
    #CASE Label_0x0201EA90
    #CASE Label_0x0201EAA6
    #CASE Label_0x0201EAAC
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EAB8
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EB28
    #CASE Label_0x0201EB28
    #CASE Label_0x0201E9AC
    #CASE Label_0x0201E9BA
    #CASE Label_0x0201E92A
    #CASE Label_0x0201EAB2

Label_0x0201E60C:
    add     sp, #0x08
    str     r4, [r7, #0x00]
    pop     {r3-r7, pc}

Label_0x0201E612:
    ldrh    r2, [r7, #0x04]
    mov     r0, #1
    add     sp, #0x08
    bic     r2, r0
    lsl     r0, r4, #16
    lsr     r1, r0, #16
    mov     r0, #1
    and     r0, r1
    orr     r0, r2
    strh    r0, [r7, #0x04]
    pop     {r3-r7, pc}

Label_0x0201E628:
    ldrh    r1, [r7, #0x04]
    mov     r0, #2
    add     sp, #0x08
    bic     r1, r0
    lsl     r0, r4, #16
    lsr     r0, r0, #16
    lsl     r0, r0, #31
    lsr     r0, r0, #30
    orr     r0, r1
    strh    r0, [r7, #0x04]
    pop     {r3-r7, pc}

Label_0x0201E63E:
    ldrh    r1, [r7, #0x04]
    mov     r0, #4
    add     sp, #0x08
    bic     r1, r0
    lsl     r0, r4, #16
    lsr     r0, r0, #16
    lsl     r0, r0, #31
    lsr     r0, r0, #29
    orr     r0, r1
    strh    r0, [r7, #0x04]
    pop     {r3-r7, pc}

Label_0x0201E654:
    add     sp, #0x08
    strh    r4, [r7, #0x06]
    pop     {r3-r7, pc}

Label_0x0201E65A:
    add     sp, #0x08
    strh    r4, [r6, #0x00]
    pop     {r3-r7, pc}

Label_0x0201E660:
    ldr     r0, =0x027E
    cmp     r4, r0
    bls     Label_0x0201E668
    b       Label_0x0201EB28

Label_0x0201E668:
    add     sp, #0x08
    strh    r4, [r6, #0x02]
    pop     {r3-r7, pc}

Label_0x0201E66E:
    add     sp, #0x08
    str     r4, [r6, #0x04]
    pop     {r3-r7, pc}

Label_0x0201E674:
    add     sp, #0x08
    str     r4, [r6, #0x08]
    pop     {r3-r7, pc}

Label_0x0201E67A:
    add     sp, #0x08
    strb    r4, [r6, #0x0C]
    pop     {r3-r7, pc}

Ability:
    strb    r4, [r6, #0x0D]
    
    ldrb    r0, [r6, #0x0E]
    lsl     r0, #26
    lsr     r0, #2
    
    lsr     r4, #8
    lsl     r4, #6
    orr     r4, r0
    strb    r4, [r6, #0x0E]
    
    add     sp, #0x08
    pop     {r3-r7, pc}

Markings:
    ldrb    r0, [r6, #0x0E]
    lsr     r0, #6
    lsl     r0, #6
    orr     r4, r0
    strb    r4, [r6, #0x0E]
    
    add     sp, #0x08
    pop     {r3-r7, pc}

Label_0x0201E68C:
    add     sp, #0x08
    strb    r4, [r6, #0x0F]
    pop     {r3-r7, pc}

Label_0x0201E692:
    ldrb    r5, [r6, #0x10]
    ldrb    r7, [r6, #0x11]
    ldrb    r0, [r6, #0x12]
    ldrb    r1, [r6, #0x13]
    add     r7, r5, r7
    add     r0, r0, r7
    ldrb    r2, [r6, #0x14]
    add     r0, r1, r0
    ldrb    r3, [r6, #0x15]
    add     r0, r2, r0
    add     r1, r3, r0
    ldr     r0, =0x01FE
    sub     r1, r0, r1
    sub     r0, r4, r5
    cmp     r0, r1
    ble     Label_0x0201E6B4
    add     r4, r5, r1

Label_0x0201E6B4:
    cmp     r4, #255
    bls     Label_0x0201E6BA
    mov     r4, #255

Label_0x0201E6BA:
    add     sp, #0x08
    strb    r4, [r6, #0x10]
    pop     {r3-r7, pc}

Label_0x0201E6C0:
    ldrb    r5, [r6, #0x11]
    ldrb    r7, [r6, #0x10]
    ldrb    r0, [r6, #0x12]
    ldrb    r1, [r6, #0x13]
    add     r7, r7, r5
    add     r0, r0, r7
    ldrb    r2, [r6, #0x14]
    add     r0, r1, r0
    ldrb    r3, [r6, #0x15]
    add     r0, r2, r0
    add     r1, r3, r0
    ldr     r0, =0x01FE
    sub     r1, r0, r1
    sub     r0, r4, r5
    cmp     r0, r1
    ble     Label_0x0201E6E2
    add     r4, r5, r1

Label_0x0201E6E2:
    cmp     r4, #255
    bls     Label_0x0201E6E8
    mov     r4, #255

Label_0x0201E6E8:
    add     sp, #0x08
    strb    r4, [r6, #0x11]
    pop     {r3-r7, pc}

Label_0x0201E6EE:
    ldrb    r7, [r6, #0x10]
    ldrb    r5, [r6, #0x11]
    ldrb    r3, [r6, #0x12]
    ldrb    r0, [r6, #0x13]
    add     r5, r7, r5
    add     r5, r3, r5
    ldrb    r1, [r6, #0x14]
    add     r0, r0, r5
    ldrb    r2, [r6, #0x15]
    add     r0, r1, r0
    add     r1, r2, r0
    ldr     r0, =0x01FE
    sub     r1, r0, r1
    sub     r0, r4, r3
    cmp     r0, r1
    ble     Label_0x0201E710
    add     r4, r3, r1

Label_0x0201E710:
    cmp     r4, #255
    bls     Label_0x0201E716
    mov     r4, #255

Label_0x0201E716:
    add     sp, #0x08
    strb    r4, [r6, #0x12]
    pop     {r3-r7, pc}

Label_0x0201E71C:
    ldrb    r7, [r6, #0x10]
    ldrb    r5, [r6, #0x11]
    ldrb    r0, [r6, #0x12]
    ldrb    r3, [r6, #0x13]
    add     r5, r7, r5
    add     r0, r0, r5
    ldrb    r1, [r6, #0x14]
    add     r0, r3, r0
    ldrb    r2, [r6, #0x15]
    add     r0, r1, r0
    add     r1, r2, r0
    ldr     r0, =0x01FE
    sub     r1, r0, r1
    sub     r0, r4, r3
    cmp     r0, r1
    ble     Label_0x0201E73E
    add     r4, r3, r1

Label_0x0201E73E:
    cmp     r4, #255
    bls     Label_0x0201E744
    mov     r4, #255

Label_0x0201E744:
    add     sp, #0x08
    strb    r4, [r6, #0x13]
    pop     {r3-r7, pc}

Label_0x0201E74A:
    ldrb    r7, [r6, #0x10]
    ldrb    r5, [r6, #0x11]
    ldrb    r0, [r6, #0x12]
    ldrb    r1, [r6, #0x13]
    add     r5, r7, r5
    add     r0, r0, r5
    ldrb    r3, [r6, #0x14]
    add     r0, r1, r0
    ldrb    r2, [r6, #0x15]
    add     r0, r3, r0
    add     r1, r2, r0
    ldr     r0, =0x01FE
    sub     r1, r0, r1
    sub     r0, r4, r3
    cmp     r0, r1
    ble     Label_0x0201E76C
    add     r4, r3, r1

Label_0x0201E76C:
    cmp     r4, #255
    bls     Label_0x0201E772
    mov     r4, #255

Label_0x0201E772:
    add     sp, #0x08
    strb    r4, [r6, #0x14]
    pop     {r3-r7, pc}

Label_0x0201E778:
    ldrb    r7, [r6, #0x10]
    ldrb    r5, [r6, #0x11]
    ldrb    r0, [r6, #0x12]
    ldrb    r1, [r6, #0x13]
    add     r5, r7, r5
    add     r0, r0, r5
    ldrb    r2, [r6, #0x14]
    add     r0, r1, r0
    ldrb    r3, [r6, #0x15]
    add     r0, r2, r0
    add     r1, r3, r0
    ldr     r0, =0x01FE
    sub     r1, r0, r1
    sub     r0, r4, r3
    cmp     r0, r1
    ble     Label_0x0201E79A
    add     r4, r3, r1

Label_0x0201E79A:
    cmp     r4, #255
    bls     Label_0x0201E7A0
    mov     r4, #255

Label_0x0201E7A0:
    add     sp, #0x08
    strb    r4, [r6, #0x15]
    pop     {r3-r7, pc}

Label_0x0201E7A6:
    add     sp, #0x08
    strb    r4, [r6, #0x16]
    pop     {r3-r7, pc}

Label_0x0201E7AC:
    add     sp, #0x08
    strb    r4, [r6, #0x17]
    pop     {r3-r7, pc}

Label_0x0201E7B2:
    add     sp, #0x08
    strb    r4, [r6, #0x18]
    pop     {r3-r7, pc}

Label_0x0201E7B8:
    add     sp, #0x08
    strb    r4, [r6, #0x19]
    pop     {r3-r7, pc}

Label_0x0201E7BE:
    add     sp, #0x08
    strb    r4, [r6, #0x1A]
    pop     {r3-r7, pc}

Label_0x0201E7C4:
    add     sp, #0x08
    strb    r4, [r6, #0x1B]
    pop     {r3-r7, pc}

Label_0x0201E7CA:
    ldr     r1, [sp, #0x00]
    mov     r0, #1
    sub     r1, #25
    lsl     r0, r1
    str     r1, [sp, #0x00]
    cmp     r4, #0
    beq     Label_0x0201E7E2
    ldr     r1, [r6, #0x1C]
    add     sp, #0x08
    orr     r0, r1
    str     r0, [r6, #0x1C]
    pop     {r3-r7, pc}

Label_0x0201E7E2:
    mov     r1, #3
    sub     r1, r1, #4
    ldr     r2, [r6, #0x1C]
    eor     r0, r1
    and     r0, r2
    add     sp, #0x08
    str     r0, [r6, #0x1C]
    pop     {r3-r7, pc}

Label_0x0201E7F2:
    ldr     r0, [sp, #0x00]
    sub     r0, #54
    str     r0, [sp, #0x00]
    lsl     r0, r0, #1
    add     sp, #0x08
    strh    r4, [r5, r0]
    pop     {r3-r7, pc}

Label_0x0201E800:
    ldr     r0, [sp, #0x00]
    sub     r0, #58
    str     r0, [sp, #0x00]
    add     r0, r5, r0
    add     sp, #0x08
    strb    r4, [r0, #0x08]
    pop     {r3-r7, pc}

Label_0x0201E80E:
    ldr     r0, [sp, #0x00]
    sub     r0, #62
    str     r0, [sp, #0x00]
    add     r0, r5, r0
    add     sp, #0x08
    strb    r4, [r0, #0x0C]
    pop     {r3-r7, pc}

Label_0x0201E81C:
    ldr     r1, [r5, #0x10]
    mov     r0, #31
    bic     r1, r0
    mov     r0, #31
    and     r0, r4
    orr     r0, r1
    add     sp, #0x08
    str     r0, [r5, #0x10]
    pop     {r3-r7, pc}

Label_0x0201E82E:
    ldr     r1, [r5, #0x10]
    ldr     r0, =0xFFFFFC1F
    add     sp, #0x08
    and     r1, r0
    lsl     r0, r4, #27
    lsr     r0, r0, #22
    orr     r0, r1
    str     r0, [r5, #0x10]
    pop     {r3-r7, pc}

Label_0x0201E840:
    ldr     r1, [r5, #0x10]
    ldr     r0, =0xFFFF83FF
    add     sp, #0x08
    and     r1, r0
    lsl     r0, r4, #27
    lsr     r0, r0, #17
    orr     r0, r1
    str     r0, [r5, #0x10]
    pop     {r3-r7, pc}

Label_0x0201E852:
    ldr     r1, [r5, #0x10]
    ldr     r0, =0xFFF07FFF
    add     sp, #0x08
    and     r1, r0
    lsl     r0, r4, #27
    lsr     r0, r0, #12
    orr     r0, r1
    str     r0, [r5, #0x10]
    pop     {r3-r7, pc}

Label_0x0201E864:
    ldr     r1, [r5, #0x10]
    ldr     r0, =0xFE0FFFFF
    add     sp, #0x08
    and     r1, r0
    lsl     r0, r4, #27
    lsr     r0, r0, #7
    orr     r0, r1
    str     r0, [r5, #0x10]
    pop     {r3-r7, pc}

Label_0x0201E876:
    ldr     r1, [r5, #0x10]
    ldr     r0, =0xC1FFFFFF
    add     sp, #0x08
    and     r1, r0
    lsl     r0, r4, #27
    lsr     r0, r0, #2
    orr     r0, r1
    str     r0, [r5, #0x10]
    pop     {r3-r7, pc}

Label_0x0201E888:
    ldr     r1, [r5, #0x10]
    ldr     r0, =0xBFFFFFFF
    add     sp, #0x08
    and     r1, r0
    lsl     r0, r4, #31
    lsr     r0, r0, #1
    orr     r0, r1
    str     r0, [r5, #0x10]
    pop     {r3-r7, pc}

Label_0x0201E89A:
    ldr     r1, [sp, #0x00]
    mov     r0, #1
    sub     r1, #77
    lsl     r0, r1
    str     r1, [sp, #0x00]
    cmp     r4, #0
    beq     Label_0x0201E8B2
    ldr     r1, [r5, #0x14]
    add     sp, #0x08
    orr     r0, r1
    str     r0, [r5, #0x14]
    pop     {r3-r7, pc}

Label_0x0201E8B2:
    mov     r1, #3
    sub     r1, r1, #4
    ldr     r2, [r5, #0x14]
    eor     r0, r1
    and     r0, r2
    add     sp, #0x08
    str     r0, [r5, #0x14]
    pop     {r3-r7, pc}

Label_0x0201E8C2:
    ldrb    r2, [r5, #0x18]
    mov     r0, #1
    add     sp, #0x08
    bic     r2, r0
    lsl     r0, r4, #24
    lsr     r1, r0, #24
    mov     r0, #1
    and     r0, r1
    orr     r0, r2
    strb    r0, [r5, #0x18]
    pop     {r3-r7, pc}

Label_0x0201E8D8:
    ldrb    r1, [r5, #0x18]
    ldrh    r0, [r6, #0x00]
    ldr     r2, [r7, #0x00]
    lsl     r1, r1, #24
    lsr     r1, r1, #27
    bl      ARM9::ReduceGenderType
    lsl     r0, r0, #30
    ldrb    r2, [r5, #0x18]
    mov     r1, #6
    lsr     r0, r0, #29
    bic     r2, r1
    orr     r0, r2
    add     sp, #0x08
    strb    r0, [r5, #0x18]
    pop     {r3-r7, pc}

Label_0x0201E8F8:
    ldrb    r1, [r5, #0x18]
    mov     r0, #248
    add     sp, #0x08
    bic     r1, r0
    lsl     r0, r4, #27
    lsr     r0, r0, #24
    orr     r0, r1
    strb    r0, [r5, #0x18]
    pop     {r3-r7, pc}

Label_0x0201E90E:
    add     sp, #0x08
    strb    r4, [r5, #0x19]
    pop     {r3-r7, pc}

Label_0x0201E914:
    ldrh    r2, [r5, #0x1A]
    mov     r0, #1
    add     sp, #0x08
    bic     r2, r0
    lsl     r0, r4, #16
    lsr     r1, r0, #16
    mov     r0, #1
    and     r0, r1
    orr     r0, r2
    strh    r0, [r5, #0x1A]
    pop     {r3-r7, pc}

Label_0x0201E92A:
    ldrh    r1, [r5, #0x1A]
    mov     r0, #2
    add     sp, #0x08
    bic     r1, r0
    lsl     r0, r4, #31
    lsr     r0, r0, #30
    orr     r0, r1
    strh    r0, [r5, #0x1A]
    pop     {r3-r7, pc}

Label_0x0201E940:
    add     sp, #0x08
    str     r4, [r5, #0x1C]
    pop     {r3-r7, pc}

Label_0x0201E946:
    ldr     r1, [sp, #0x04]
    mov     r0, r4
    mov     r2, #11
    bl      ARM9::GFL_StrBufStoreString
    mov     r0, r7
    bl      ARM9::IsPokeNicknamed
    ldr     r2, [r5, #0x10]
    ldr     r1, =0x7FFFFFFF
    lsl     r0, r0, #31
    and     r1, r2
    orr     r0, r1
    add     sp, #0x08
    str     r0, [r5, #0x10]
    pop     {r3-r7, pc}

Label_0x0201E966:
    ldr     r1, [sp, #0x04]
    mov     r0, r4
    mov     r2, #11
    bl      ARM9::WCharsNCopy
    mov     r0, r7
    bl      ARM9::IsPokeNicknamed
    ldr     r2, [r5, #0x10]
    ldr     r1, =0x7FFFFFFF
    b       Label_0x0201E9A0
    
    dcd     0x027E
    dcd     0x01FE
    dcd     0xFFFFFC1F
    dcd     0xFFFF83FF
    dcd     0xFFF07FFF
    dcd     0xFE0FFFFF
    dcd     0xC1FFFFFF
    dcd     0xBFFFFFFF
    dcd     0x7FFFFFFF

Label_0x0201E9A0:
    lsl     r0, r0, #31
    and     r1, r2
    orr     r0, r1
    add     sp, #0x08
    str     r0, [r5, #0x10]
    pop     {r3-r7, pc}

Label_0x0201E9AC:
    ldr     r1, [sp, #0x04]
    mov     r0, r4
    mov     r2, #11
    bl      ARM9::GFL_StrBufStoreString
    add     sp, #0x08
    pop     {r3-r7, pc}

Label_0x0201E9BA:
    ldr     r1, [sp, #0x04]
    mov     r0, r4
    mov     r2, #11
    bl      ARM9::WCharsNCopy
    add     sp, #0x08
    pop     {r3-r7, pc}

Label_0x0201E9C8:
    ldr     r0, [sp, #0x04]
    add     sp, #0x08
    strb    r4, [r0, #0x16]
    pop     {r3-r7, pc}

Label_0x0201E9D0:
    ldr     r0, [sp, #0x04]
    add     sp, #0x08
    strb    r4, [r0, #0x17]
    pop     {r3-r7, pc}

Label_0x0201E9D8:
    ldr     r0, [sp, #0x00]
    mov     r1, #1
    sub     r0, #120
    lsl     r1, r0
    str     r0, [sp, #0x00]
    asr     r0, r1, #31
    cmp     r4, #0
    beq     Label_0x0201EA00
    ldr     r2, [sp, #0x04]
    ldr     r3, [sp, #0x04]
    add     r2, #24
    ldr     r4, [r3, #0x18]
    ldr     r5, [r2, #0x04]
    orr     r4, r1
    orr     r5, r0
    mov     r0, r3
    str     r4, [r0, #0x18]
    add     sp, #0x08
    str     r5, [r2, #0x04]
    pop     {r3-r7, pc}

Label_0x0201EA00:
    ldr     r3, [sp, #0x04]
    ldr     r2, [sp, #0x04]
    add     r3, #24
    ldr     r4, [r2, #0x18]
    mov     r2, #3
    sub     r2, r2, #4
    mov     r6, #3
    sub     r6, r6, #4
    eor     r0, r2
    ldr     r5, [r3, #0x04]
    eor     r1, r6
    and     r5, r0
    mov     r2, r4
    ldr     r0, [sp, #0x04]
    and     r2, r1
    str     r2, [r0, #0x18]
    add     sp, #0x08
    str     r5, [r3, #0x04]
    pop     {r3-r7, pc}

Label_0x0201EA26:
    mov     r0, r4
    mov     r2, #8
    bl      ARM9::GFL_StrBufStoreString
    add     sp, #0x08
    pop     {r3-r7, pc}

Label_0x0201EA32:
    mov     r0, r4
    mov     r2, #8
    bl      ARM9::WCharsNCopy
    add     sp, #0x08
    pop     {r3-r7, pc}

Label_0x0201EA3E:
    add     sp, #0x08
    strb    r4, [r1, #0x10]
    pop     {r3-r7, pc}

Label_0x0201EA44:
    add     sp, #0x08
    strb    r4, [r1, #0x11]
    pop     {r3-r7, pc}

Label_0x0201EA4A:
    add     sp, #0x08
    strb    r4, [r1, #0x12]
    pop     {r3-r7, pc}

Label_0x0201EA50:
    add     sp, #0x08
    strb    r4, [r1, #0x13]
    pop     {r3-r7, pc}

Label_0x0201EA56:
    add     sp, #0x08
    strb    r4, [r1, #0x14]
    pop     {r3-r7, pc}

Label_0x0201EA5C:
    add     sp, #0x08
    strb    r4, [r1, #0x15]
    pop     {r3-r7, pc}

Label_0x0201EA62:
    add     sp, #0x08
    strh    r4, [r1, #0x16]
    pop     {r3-r7, pc}

Label_0x0201EA68:
    add     sp, #0x08
    strh    r4, [r1, #0x18]
    pop     {r3-r7, pc}

Label_0x0201EA6E:
    add     sp, #0x08
    strb    r4, [r1, #0x1A]
    pop     {r3-r7, pc}

Label_0x0201EA74:
    add     sp, #0x08
    strb    r4, [r1, #0x1B]
    pop     {r3-r7, pc}

Label_0x0201EA7A:
    ldrb    r3, [r1, #0x1C]
    mov     r0, #127
    add     sp, #0x08
    bic     r3, r0
    lsl     r0, r4, #24
    lsr     r2, r0, #24
    mov     r0, #127
    and     r0, r2
    orr     r0, r3
    strb    r0, [r1, #0x1C]
    pop     {r3-r7, pc}

Label_0x0201EA90:
    ldrb    r2, [r1, #0x1C]
    mov     r0, #128
    add     sp, #0x08
    bic     r2, r0
    lsl     r0, r4, #31
    lsr     r0, r0, #24
    orr     r0, r2
    strb    r0, [r1, #0x1C]
    pop     {r3-r7, pc}

Label_0x0201EAA6:
    add     sp, #0x08
    strb    r4, [r1, #0x1D]
    pop     {r3-r7, pc}

Label_0x0201EAAC:
    add     sp, #0x08
    strb    r4, [r1, #0x1E]
    pop     {r3-r7, pc}

Label_0x0201EAB2:
    add     sp, #0x08
    strb    r4, [r1, #0x1F]
    pop     {r3-r7, pc}

Label_0x0201EAB8:
    ldr     r1, [r5, #0x10]
    mov     r0, #31
    bic     r1, r0
    mov     r0, #31
    mov     r2, r4
    and     r2, r0
    lsl     r2, r2, #24
    lsr     r2, r2, #24
    and     r2, r0
    orr     r2, r1
    ldr     r1, =0xFFFFFC1F
    lsr     r3, r4, #20
    and     r1, r2
    lsr     r2, r4, #5
    and     r2, r0
    lsl     r2, r2, #27
    lsr     r2, r2, #22
    orr     r2, r1
    ldr     r1, =0xFFFF83FF
    and     r3, r0
    and     r1, r2
    lsr     r2, r4, #10
    and     r2, r0
    lsl     r2, r2, #27
    lsr     r2, r2, #17
    orr     r2, r1
    ldr     r1, =0xFE0FFFFF
    lsl     r3, r3, #24
    and     r1, r2
    lsr     r2, r4, #15
    and     r2, r0
    lsl     r2, r2, #27
    lsr     r2, r2, #7
    orr     r1, r2
    ldr     r2, =0xC1FFFFFF
    lsr     r3, r3, #24
    lsl     r3, r3, #27
    and     r1, r2
    lsr     r3, r3, #2
    orr     r3, r1
    asr     r1, r2, #10
    lsr     r2, r4, #25
    and     r0, r2
    lsl     r0, r0, #27
    and     r1, r3
    lsr     r0, r0, #12
    orr     r0, r1
    str     r0, [r5, #0x10]

Label_0x0201EB28:
    add     sp, #0x08
    pop     {r3-r7, pc}
    
    dcd     0xFFFFFC1F
    dcd     0xFFFF83FF
    dcd     0xFE0FFFFF
    dcd     0xC1FFFFFF