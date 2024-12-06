    push    {r3-r4, lr}
    mov     r3, r0
    
    cmp     r1, #0
    beq     GetData
    cmp     r1, #2
    bhi     ReturnNull
    b       FindSpritePalette
    
    
GetData:
    mov     r0, #24 ; a/0/2/4
    mov     r1, r3
    bl      ARM9::GFL_ArcSysReadHeapNew
    pop     {r3, pc}
    

FindSpritePalette:
    lsl     r4, r1, #1
    
    ldr     r0, =Data_ItemSpriteMap
    ldr     r1, =ITEM_SPRITES_PALETTES_COUNT
    mov     r2, #6
    bl      ARM9::BinarySearch_Half
    lsl     r1, r0, #6
    add     r1, r4
    ldr     r0, =Data_ItemSpriteMap
    ldrh    r1, [r0, r1]
    mov     r0, #25 ; a/0/2/5
    bl      ARM9::GFL_ArcSysReadHeapNew
    pop     {r3-r4, pc}
    
    
ReturnNull:
    mov     r0, #0
    pop     {r3-r4, pc}
    