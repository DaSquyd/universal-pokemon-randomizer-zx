    push    {r3, lr}
    cmp     r1, #ITEMGET_MAX
    bhi     ReturnZero
    
    #switch r1
    #case ItemGet_Data
    #case ItemGet_Sprite
    #case ItemGet_Palette
    #case ItemGet_ShooterSprite
    #case ItemGet_ShooterPalette
    
    
ItemGet_Data:
    cmp     r0, #0 ; null item
    beq     ReturnZero
    
    ldr     r1, =0xFFFF ; -1
    cmp     r0, r1
    beq     ReturnZero
    pop     {r3, pc}
    
    
ItemGet_Sprite:
    ldr     r1, =0xFFFF ; -1
    cmp     r0, r1
    bne     ItemGet_Sprite_Find
    lsr     r0, r1, #6 ; r0 := 1023
    pop     {r3, pc}
    
ItemGet_Sprite_Find:
    mov     r4, #2
    b       ItemGet_Find
    
    
ItemGet_Palette:
    ldr     r1, =0xFFFF ; -1
    cmp     r0, r1
    bne     ItemGet_Palette_Find
    mov     r0, #(0x0400 >> 8)
    lsl     r0, #8
    pop     {r3, pc}
    
ItemGet_Palette_Find:
    mov     r4, #4
    
    
ItemGet_Find:
    mov     r3, r0
    ldr     r0, =ARM9::Data_ItemSpriteMap
    ldr     r1, =ITEM_SPRITES_PALETTES_COUNT
    mov     r2, #6
    bl      ARM9::BinarySearch_Half
    lsl     r1, r0, #6
    add     r1, r4
    ldr     r0, =ARM9::Data_ItemSpriteMap
    ldrh    r0, [r0, r1]
    pop     {r3, pc}
    
    
ItemGet_ShooterSprite:
    mov     r4, #0
    b       ItemGet_Shooter
    
    
ItemGet_ShooterPalette:
    mov     r4, #2
    
    
ItemGet_Shooter:
    bl      ARM9::ShooterItem_GetTableIndex
    lsl     r1, r2, #2
    ldr     r0, =ARM9::Data_ItemSpriteMap
    add     r0, r4
    ldrh    r0, [r0, r1]
    pop     {r3, pc}
    
    
ReturnZero:
    mov     r0, #0
    pop     {r3, pc}