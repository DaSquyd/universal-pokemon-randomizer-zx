    push    {r3-r5, lr}
    mov     r5, r0
    mov     r2, #13
    mov     r4, r1
    bl      StorageSystem::Unk_21D06E4
    ldr     r0, [r5, #0x2C]
    mov     r1, r4
    bl      StorageSystem::DisplayPreview_TypeGraphics
    mov     r0, r5
    mov     r1, r4
    bl      StorageSystem::PreviewCore
    ldrb    r1, [r4, #Box2Main.markings]
    mov     r0, r5
    bl      StorageSystem::Unk_21BF3A4
    ldr     r0, [r5, #0x2C]
    mov     r1, r4
    bl      StorageSystem::Unk_21BF3CC
    mov     r0, #5
    mov     r1, #3
    mov     r2, #0
    bl      ARM9::GFL_BGSysMoveBGReq
    pop     {r3-r5, pc}