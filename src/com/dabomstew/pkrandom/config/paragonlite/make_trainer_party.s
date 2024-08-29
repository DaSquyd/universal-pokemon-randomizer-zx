#DEFINE TRUE 1
#DEFINE FALSE 0

#DEFINE DATA                    0x00
#DEFINE POKE_IVS                0x04
#DEFINE TR_FLAGS_OFFSET         0x08
#DEFINE UNKNOWN                 0x0C
#DEFINE LCG_STATE_LOW           0x10
#DEFINE LCG_STATE_HIGH          0x14

#DEFINE TRAINER_NUM             0x18
#DEFINE PARTY_BLOCK             0x1C
#DEFINE BLOCK_GROUP_ID          0x20

#DEFINE TRAINER_FILE            0x24
#DEFINE TR_POKE_FILE            0x28
#DEFINE PARTY_POKES             0x2C

#DEFINE POOL_SIZE               0x30

#DEFINE POKE_SIZE               0x34
#DEFINE POKE_ITEM_OFFSET        0x38
#DEFINE POKE_MOVES_OFFSET       0x3C
#DEFINE POKE_EVS_OFFSET         0x40
#DEFINE POKE_RANDOM_MODE        0x44
    
#DEFINE POKE_INDEX              0x48
#DEFINE POKE_OFFSET             0x4C
#DEFINE POKE_ID                 0x50
#DEFINE POKE_SPECIES            0x54
#DEFINE POKE_LEVEL              0x58
#DEFINE POKE_DIFFICULTY         0x5C
#DEFINE POKE_FLAGS              0x60
#DEFINE POKE_RANDOM_ID          0x64

#DEFINE SELECTED_POKES          0x68 ; 8 bytes, only first 6 are used (one for each index)
#DEFINE SELECTED_COUNT          0x70

#DEFINE CHANNEL                 0x74

#DEFINE BUFFER                  0x78
#DEFINE BUFFER_MAX_SIZE         32
#DEFINE BUFFER_SIZE             BUFFER + BUFFER_MAX_SIZE

#DEFINE __MAX__                 BUFFER_SIZE + 4

; pre-DEFd
#DEFINE PLAYER_BASE_BLOCK       __MAX__ + 0x18 ; {r3,r4,r5,r6,r7,lr} take up the other 24 bytes

    push    {r3-r7, lr}
    sub     sp, #__MAX__
    str     r0, [sp, #TRAINER_ID]
    str     r1, [sp, #PARTY_BLOCK]
    str     r2, [sp, #UNKNOWN_R2]
    
    mov     r0, r1 ; PARTY_BLOCK
    mov     r1, #6 ; count
    bl      ARM9::PrepareFullPartyBlock
    
    ldr     r6, =ARM9::Data_tr_tool
    
    ldr     r5, =0x023D
    str     r5, [sp, #DATA]
    
    ldr     r2, [sp, #UNKNOWN_R2]
    ldr     r1, =0x7FFF ; Mask: 01111111_11111111
    and     r2, r1
    
    add     r0, r1, #1 ; 10000000_00000000
    mov     r4, r2
    orr     r4, r0
    
    lsl     r0, r4, #16
    lsr     r0, #16
    mov     r1, #20 ; block size
    mov     r2, #0 ; clear block = false
    mov     r3, r6 ; tr_tool.c
    bl      ARM9::AllocateBlockFromExpHeap
    str     r0, [sp, #TRAINER_FILE]
    add     r0, r5, #1
    str     r0, [sp, #DATA]
    
    lsl     r0, r4, #16
    lsr     r0, #16
    mov     r1, #18
    lsl     r1, #5 ; block size = 576 (18 * 32)
    mov     r2, #0 ; clear block = false
    mov     r3, r6 ; tr_tool.c
    bl      ARM9::AllocateBlockFromExpHeap
    str     r0, [sp, #TR_POKE_FILE]
    
    bl      ARM9::GetPartyBlockSize ; always returns 220 (36 * 6)
    mov     r1, r0 ; block size
    add     r0, r5, #2
    str     r0, [sp, #DATA]
    
    lsl     r0, r4, #16
    lsr     r0, #16
    mov     r2, #0 ; clear block = false
    mov     r3, r6 ; tr_tool.c
    bl      ARM9::AllocateBlockFromExpHeap
    str     r0, [sp, #PARTY_POKES]
    
    lsl     r0, r4, #16
    lsr     r0, #16
    mov     r1, #32
    mov     r2, #0 ; clear block = false
    mov     r3, r6 ; tr_tool.c
    bl      ARM9::AllocateBlockFromExpHeap
    str     r0, [sp, #SELECTABLE_POKES]
    
    ldr     r0, [sp, #TRAINER_ID]
    ldr     r1, [sp, #TRAINER_FILE]
    bl      ARM9::LoadTrainerFile
    
    ldr     r0, [sp, #TRAINER_ID]
    ldr     r1, [sp, #TR_POKE_FILE]
    bl      ARM9::LoadTrainerPokeFile
    
    ldr     r0, [sp, #TRAINER_FILE]
    ldrb    r0, [r0, #1] ; trainer class
    bl      ARM9::GetTrainerClassGender ; gender is determined by trainer class; 0=male, 1=female
    mov     r1, #120
    cmp     r0, #0
    bne     StorePokeFlagOffset
    mov     r1, #136    
StorePokeFlagOffset:
    str     r1, [sp, #TR_FLAGS_OFFSET]
    

    ; Normally, Trainer Flags are 000000IM where I = custom items and M = custom moves
    ; our updated version is 0RREIIMM
    ;   M: move mode    0=Level, 1=Custom, 2=Random, 3=RandomTMs
    ;   I: item mode    0=NoItems, 1=Custom, 2=RandomEasy, 3=RandomHard
    ;   E: custom evs
    ;   R: random mode  0=Off, 2=Locked (based solely on TID), 3=Always (random every time)
    #DEFINE MOVE_MODE_LEVEL 0
    #DEFINE MOVE_MODE_CUSTOM 1
    #DEFINE MOVE_MODE_RANDOM 2
    #DEFINE MOVE_MODE_RANDOM_TMS 3
    
    #DEFINE ITEM_MODE_NONE 0
    #DEFINE ITEM_MODE_CUSTOM 1
    #DEFINE ITEM_MODE_RANDOM_EASY 2
    #DEFINE ITEM_MODE_RANDOM_HARD 3
    
    #DEFINE RANDOM_MODE_OFF 0
    #DEFINE RANDOM_MODE_LOCKED 2
    #DEFINE RANDOM_MODE_ALWAYS 3

    ldr     r0, [sp, #TRAINER_FILE]
    ldrb    r5, [r0] ; trainer flags
    mov     r1, #0x08 ; POKE_SIZE
    
ItemMode:
    mov     r0, #-1
    str     r0, [sp, #POKE_ITEM_OFFSET]
    lsl     r0, r5, #28
    lsr     r0, #30
    cmp     r0, #ITEM_MODE_CUSTOM
    bne     MoveMode
    str     r1, [sp, #POKE_ITEM_OFFSET]
    add     r1, #2

MoveMode:
    mov     r0, #-1
    str     r0, [sp, #POKE_MOVES_OFFSET]
    lsl     r0, r5, #30
    lsr     r0, #30
    cmp     r0, #MOVE_MODE_CUSTOM
    bne     EVs
    str     r1, [sp, #POKE_MOVES_OFFSET]
    add     r1, #8
    
EVs:
    mov     r0, #-1
    str     r0, [sp, #POKE_EVS_OFFSET]
    lsl     r0, r5, #27
    lsr     r0, #31
    cmp     r0, #FALSE
    beq     RandomMode
    str     r1, [sp, #POKE_EVS_OFFSET]
    add     r1, #2

RandomMode:
    lsl     r0, r5, #25
    lsr     r0, #30
    str     r0, [sp, #POKE_RANDOM_MODE]
    
StorePokeSize:
    str     r1, [sp, #POKE_SIZE]
    

ClearSelectedPokes:
    mov     r0, #0
    str     r0, [sp, #SELECTED_COUNT]


SetUpLCG:
    mov     r0, [sp, #PLAYER_BASE_BLOCK]
    bl      ARM9::JumpToPlayerBaseBlockInfo
    bl      ARM9::GetIDAsUInt
    ldr     r1, [sp, #TRAINER_NUM]
    add     r0, r1
    str     r0, [sp, #LCG_STATE_LOW]
    mov     r1, #0
    str     r1, [sp, #LCG_STATE_HIGH]
    bl      ARM9::SeedLCG
    str     r0, [sp, #LCG_STATE_LOW]
    str     r1, [sp, #LCG_STATE_HIGH]


SetUpChannelLoop:
    ldr     r0, [sp, #TRAINER_FILE]
    ldrb    r1, [r0, #3]
    str     r1, [sp, #POOL_SIZE]
    mov     r0, #0 ; index
    str     r0, [sp, #CHANNEL]
    
ChannelLoopStart:
    mov     r0, #0
    str     r0, [sp, #BUFFER_SIZE]
    str     r0, [sp, #POKE_INDEX]
    
    ; TODO: Make it so channels select the desired number of Pokémon from the pool
    
ChannelLoop_PokeLoopStart:
    ldr     r0, [sp, #POKE_INDEX]
    ldr     r1, [sp, #POKE_SIZE]
    mul     r1, r0
    ldr     r0, [sp, #TR_POKE_FILE]
    add     r1, r0
    ldrb    r0, [r1, #3] ; channel
    ldr     r1, [sp, #CHANNEL]
    cmp     r0, r1
    bne     ChannelLoop_PokeLoopEnd ; this poke doesn't use this channel
    
; add poke to buffer
    ldr     r0, [sp, #POKE_INDEX]
    ldr     r1, [sp, #BUFFER]
    ldr     r2, [sp, #BUFFER_SIZE]
    str     r0, [r1, r2]
    add     r2, #1
    str     r2, [sp, #BUFFER_SIZE]    

ChannelLoop_PokeLoopEnd:
    ldr     r0, [sp, #POKE_INDEX]
    add     r0, #1
    str     r0, [sp, #POKE_INDEX]
    ldr     r1, [sp, #POOL_SIZE]
    cmp     r0, r1
    bcc     ChannelLoop_PokeLoopStart
    
; Select a Pokémon from the buffer...
; First, choose a random algo
    ldr     r0, [sp, #POKE_RANDOM_MODE]
    cmp     r0, #RANDOM_MODE_LOCKED
    bne     ChannelLoop_Mersenne

ChannelLoop_LCG:
    ldr     r0, [sp, #LCG_STATE_LOW]
    ldr     r1, [sp, #LCG_STATE_HIGH]
    bl      ARM9::LCG
    mov     r0, r1
    b       ChannelLoop_SelectPoke
    
ChannelLoop_Mersenne:
    bl      ARM9::MersenneTwister
    
ChannelLoop_SelectPoke:
    ldr     r0, [sp, #TRAINER_FILE]
    ldrb    r1, [r0, #3]

ChannelLoopEnd:
    ldr     r0, [sp, #CHANNEL]
    add     r0, #1
    str     r0, [sp, #CHANNEL]
    cmp     r0, #6
    bcc     ChannelLoopStart
    
    





    
    
    
    
    
    
    
    
    
    
    
    
    
; ACE
FindAces:
    mov     r0, #0
    str     r0, [sp, #POKE_INDEX]
    mov     r3, #0 ; write_offset
    
FindAcesLoopStart:
    ldr     r2, [sp, #POKE_INDEX]
    ldr     r1, [sp, #POKE_SIZE]
FindAcesLoopEnd:
    add     r2, #1
    str     r2, [sp, #POKE_INDEX]
    ldr     r1, [sp, #POKE_COUNT]
    cmp     r0, r1
    blt     FindAcesLoopStart
    
    
; LEADS
FindLeads:
    mov     r0, #0
    str     r0, [sp, #POKE_INDEX]
    mov     r3, #0 ; write_offset
    
FindLeadsLoopStart:
    ldr     r2, [sp, #POKE_INDEX]
    ldr     r1, [sp, #POKE_SIZE]
    mul     r1, r2
    ldr     r0, [sp, #TR_POKE_FILE]
    add     r1, r0
    ldrb    r0, [r1, #1] ; flags
    lsl     r0, #28
    lsr     r0, #30 ; Mask: 00001100
    cmp     r0, #1
    bne     FindLeadsLoopEnd ; continue
    add     r0, sp, #SELECTABLE_POKES
    str     r2, [r0, r3]
    add     r3, #4 ; write_offset += 4
FindLeadsLoopEnd:
    add     r2, #1
    str     r2, [sp, #POKE_INDEX]
    ldr     r1, [sp, #POKE_COUNT]
    cmp     r0, r1
    blt     FindLeadsLoopStart

SelectLeads:
    mov     r3, #0 ; index
    
    ldr     r0, [sp, #TRAINER_FILE]
    ldrb    r0, [r0, #0]
    lsl     r0, #25
    lsr     r0, #30 ; Mask: 01100000
    
    
    
SetupLoop:
    mov     r0, #0
    str     r0, [sp, #POKE_INDEX] ; i := 0
    
LoopStart:
    ldr     r0, [sp, #POKE_INDEX]
    ldr     r5, [sp, #POKE_SIZE]
    mul     r5, r0
    ldr     r0, [sp, #TR_POKE_FILE]
    add     r6, r0, r5
    
    ldr     r0, [sp, #BLOCK_GROUP_ID]
    str     r0, [sp, #DATA]
    ldrh    r0, [r6, #4] ; species
    ldrh    r1, [r6, #6] ; form
    ldrb    r2, [r6, #1] ; poke flags
    add     r3, sp, #TR_FLAGS_OFFSET ; tr_flags*
    bl      ARM9::HandlerTrainerPokeFlags
    
    ldrh    r0, [r6, #4] ; species
    str     r0, [sp, #POKE_SPECIES]
    
    ldrb    r0, [r6, #2] ; level
    str     r0, [sp, #POKE_LEVEL]
    
    ldrb    r0, [r6, #3] ; random id
    str     r0, [sp, #POKE_RANDOM_ID]
    
    ldrb    r0, [r6, #0] ; difficulty
    str     r0, [sp, #POKE_DIFFICULTY]
    
    ldrb    r0, [r6, #1] ; flags
    str     r0, [sp, #POKE_FLAGS]
    
    ; Randomness is sources from the player's Trainer ID. This allows for many random factors to remain consistent
    ; within one playthrough (an NPC having the same Pokémon) but different for a other save files.
    mov     r0, [sp, #PLAYER_BASE_BLOCK]
    bl      ARM9::JumpToPlayerBaseBlockInfo
    bl      ARM9::GetIDAsUInt
    ldr     r1, [sp, #TRAINER_NUM]
    add     r0, r1
    ldr     r1, [r6, #4] ; species
    add     r0, r1
    ldrb    r1, [r6, #3] ; random_id
    add     r3, r0, r1 ; r3 := player_tid + trainer_num + poke_species + poke_random_id
    
    mov     r5, #0 ; lcg iteration
    
    ldr     r0, [sp, #TRAINER_FILE]
    ldrb    r7, [r0, #1] ; trainer class
    
    cmp     r7, #0
    ble     LinearCongruentialEnd ; trainer_class <= 0
    
SeedLinearCongruential:
    mov     r0, r3 ; seed (low)
    mov     r1, #0 ; seed (high)
    bl      ARM9::SeedLCG
    str     r0, [sp, #LCG_STATE_LOW]
    str     r1, [sp, #LCG_STATE_HIGH]
    
LinearCongruentialEnd:
    ldr     r0, [sp, #TR_FLAGS_OFFSET]
    lsl     r1, r2, #8
    add     r0, r1 ; 0x00RRRRFF, R=rand, F=flags
    str     r0, [sp, #POKE_ID]
    
IVs:
    
    mov     r5, #31
    
    
End:
    ldr     r0, [sp, #TRAINER_FILE]
    bl      ARM9::FreeBlock
    
    ldr     r0, [sp, #TR_POKE_FILE]
    bl      ARM9::FreeBlock
    
    ldr     r0, [sp, #SELECTABLE_POKES]
    bl      ARM9::FreeBlock
    
    mov     r0, r4
    bl      ARM9::FreeBlock
    
    add     sp, #__MAX__
    pop     {r3-r7, pc}