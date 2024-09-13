    push    {r3-r5, lr}
    mov     r5, r0
    mov     r2, #13
    mov     r4, r1
    bl      Unk_21D06E4
    ldr     r0, [r5, #0x2C]
    mov     r1, r4
    bl      Unk_21D0A68
    mov     r0, r5
    mov     r1, r4
    bl      Unk_21CE7D8
    ldrh    r1, [r4, #0x10]
    mov     r0, r5
    bl      Unk_21BF3A4
    ldr     r0, [r5, #0x2C]
    mov     r1, r4
    bl      Unk_21BF3CC
    mov     r0, #5
    mov     r1, #3
    mov     r2, #0
    bl      Unk_2045E48
    pop     {r3-r5, pc}