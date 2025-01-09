#define S_HeapId 0x00
#define S_MenuParam 0x04 ; size is 

#define NumButtons 3
#define MenuParamSize (UnovaLink_MenuParam.SIZE_WITHOUT_BUTTONS + UnovaLink_MenuParam_Button.SIZE * NumButtons)
#define StackSize (0x04 + MenuParamSize)

#define ButtonWidth 26
#define ButtonHeight 2
#define ButtonX 3
#define ButtonYOffset 5

    push    {r4-r7, lr}
    sub     sp, #StackSize
    mov     r4, r0
    str     r1, [sp, #S_HeapId]
    ldr     r2, [r4, #UnovaLinkWork.titlePtr]
    cmp     r2, #0
    bne     Label_0x021BFFB2
    bl      UnovaLink::Title_Create

Label_0x021BFFB2:
    add     r0, sp, #S_MenuParam
    mov     r1, #0
    mov     r2, #MenuParamSize
    mov     r5, #0
    blx     ARM9::sys_memset
    
    mov     r0, #14
    str     r0, [sp, #(S_MenuParam + UnovaLink_MenuParam.fontPalette)]
    
    mov     r1, #1
    str     r1, [sp, #(S_MenuParam + UnovaLink_MenuParam.frame)]
    str     r1, [sp, #(S_MenuParam + UnovaLink_MenuParam.framePalette)]
    
    mov     r0, #10
    str     r0, [sp, #(S_MenuParam + UnovaLink_MenuParam.frameGraphic)]
    
    ldr     r0, [r4, #UnovaLinkWork.msgPtr]
    ldr     r6, =UnovaLink::Data_aMAN
    str     r0, [sp, #(S_MenuParam + UnovaLink_MenuParam.message)]
    
    ldr     r0, [r4, #UnovaLinkWork.fontPtr]
    mov     r1, r6
    str     r0, [sp, #(S_MenuParam + UnovaLink_MenuParam.font)]
    
    mov     r0, #NumButtons
    str     r0, [sp, #(S_MenuParam + UnovaLink_MenuParam.buttonCount)]
    
    mov     r0, r4
    add     r0, #UnovaLinkWork.select
    bl      UnovaLink::Map_ContainsKey
    cmp     r0, #FALSE
    beq     Label_0x021C0000
    
    mov     r0, r4
    add     r0, #UnovaLinkWork.select
    mov     r1, r6
    bl      UnovaLink::Map_GetValue
    str     r0, [sp, #(S_MenuParam + UnovaLink_MenuParam.cursor)]
    
    mov     r0, r4
    add     r0, #UnovaLinkWork.select
    mov     r1, r6
    bl      UnovaLink::Map_Remove
    b       Label_0x021C0002

Label_0x021C0000:
    str     r5, [sp, #(S_MenuParam + UnovaLink_MenuParam.cursor)]

Label_0x021C0002:
    ldr     r0, [sp, #(S_MenuParam + UnovaLink_MenuParam.buttonCount)]
    mov     r6, #0 ; iteration
    cmp     r0, #0
    bls     Label_0x021C0040
    
    add     r5, sp, #S_MenuParam ; r5 := param
    mov     r2, #ButtonWidth
    mov     r1, #ButtonHeight
    mov     r7, #ButtonX

Label_0x021C0012:
    mov     r3, #UnovaLink_MenuParam_Button.SIZE
    mul     r3, r6
    
    add     r0, r5, r3
    add     r0, #(UnovaLinkWork.paramPtr + UnovaLink_MenuParam.buttons + UnovaLink_MenuParam_Button.width)
    strb    r2, [r0]
    
    add     r0, r5, r3
    add     r0, #(UnovaLinkWork.paramPtr + UnovaLink_MenuParam.buttons + UnovaLink_MenuParam_Button.height)
    strb    r1, [r0]
    
    add     r0, r5, r3
    add     r0, #(UnovaLinkWork.paramPtr + UnovaLink_MenuParam.buttons + UnovaLink_MenuParam_Button.x)
    strb    r7, [r0]
    
    add     r0, r5, r3
    add     r0, #(UnovaLinkWork.paramPtr + UnovaLink_MenuParam.buttons + UnovaLink_MenuParam_Button.height)
    ldrb    r0, [r0]
    add     r3, r5, r3
    add     r3, #(UnovaLinkWork.paramPtr + UnovaLink_MenuParam.buttons + UnovaLink_MenuParam_Button.y)
    add     r0, r0, #3
    mul     r0, r6
    add     r0, r0, #ButtonYOffset
    strb    r0, [r3]
    ldr     r0, [sp, #(S_MenuParam + UnovaLink_MenuParam.buttonCount)]
    add     r6, #1
    cmp     r6, r0
    bcc     Label_0x021C0012

Label_0x021C0040:
    mov     r0, #TITLETXT_UnovaLink_KeySystemButton
    str     r0, [sp, #(S_MenuParam + UnovaLink_MenuParam.buttons + UnovaLink_MenuParam_Button.strId + (UnovaLink_MenuParam_Button.SIZE * 0))]
    
    mov     r0, #TITLETXT_UnovaLink_MemoryLinkButton
    str     r0, [sp, #(S_MenuParam + UnovaLink_MenuParam.buttons + UnovaLink_MenuParam_Button.strId + (UnovaLink_MenuParam_Button.SIZE * 1))]
    
    mov     r0, #TITLETXT_UnovaLink_3dsLinkButton
    str     r0, [sp, #(S_MenuParam + UnovaLink_MenuParam.buttons + UnovaLink_MenuParam_Button.strId + (UnovaLink_MenuParam_Button.SIZE * 2))]
    
    mov     r0, #TITLETXT_UnovaLink_BackButton
    str     r0, [sp, #(S_MenuParam + UnovaLink_MenuParam.buttons + UnovaLink_MenuParam_Button.strId + (UnovaLink_MenuParam_Button.SIZE * 3))]
    
    ldr     r1, [sp, #S_HeapId]
    add     r0, sp, #S_MenuParam
    bl      UnovaLink::Menu_Init
    str     r0, [r4, #UnovaLinkWork.menuPtr]
    
    ldr     r0, [r4, #UnovaLinkWork.titlePtr]
    ldr     r1, [r4, #UnovaLinkWork.msgPtr]
    mov     r2, #TITLETXT_UnovaLink_MainMenuTitle
    mov     r3, #0
    bl      UnovaLink::Text_Print
    
    ldr     r1, [sp, #S_HeapId]
    mov     r0, r4
    bl      UnovaLink::Info_Create
    
    ldr     r0, [r4, #UnovaLinkWork.menuPtr]
    bl      UnovaLink::Menu_GetCursor
    mov     r2, r0
    
    ldr     r0, [r4, #UnovaLinkWork.infoPtr]
    ldr     r1, [r4, #UnovaLinkWork.msgPtr]
    add     r2, #TITLETXT_UnovaLink_SubMenuDescriptions
    mov     r3, #0
    bl      UnovaLink::Text_Print
    
    ldr     r0, [r4, #UnovaLinkWork.backgroundPtr]
    mov     r1, #2
    mov     r2, #0
    bl      UnovaLink::BG_TransScreen
    
    add     sp, #StackSize
    pop     {r4-r7, pc}
    