print 'Loading Resource Types...'
ResourceTypes = {}

print 'Loading Player...'
Player = {}
Player.__index = Player
local resources = {}

function Player.newResource(resourceType, initial)
    assert(type(resourceType) == 'string')
    table.insert(ResourceTypes, resourceType)
    if initial then assert(type(initial) == 'number')
    else initial = 0
    end
    resources[string.lower(resourceType)] = initial
    print("New Resource " .. resourceType .. ", initial: " .. initial)
end

function Player.getResource(resourceType)
    assert(type(resourceType) == 'string')
    resourceType = string.lower(resourceType)
    return resources[resourceType]
end

function Player.hasResource(resourceType, amt)
    assert(type(amt) == 'number')
    return Player.getResource(resourceType) >= amt
end

function Player.addResource(resourceType, amt)
    assert(type(resourceType) == 'string')
    assert(type(amt) == 'number')
    resourceType = string.lower(resourceType)
    resources[resourceType] = resources[resourceType] + amt
    --print("Added " .. amt .. " of " .. resourceType)
end

function Player.takeResource(resourceType, amt)
    assert(type(resourceType) == 'string')
    assert(type(amt) == 'number')
    resourceType = string.lower(resourceType)
    resources[resourceType] = math.max(0, resources[resourceType] - amt)
    --print("Removed " .. amt .. " of " .. resourceType)
end

Player.maxWoodDelay = 0.3333333333333333
Player.maxLivesDelay = 3
local woodDelay = Player.maxWoodDelay
local livesDelay = Player.maxLivesDelay
function Player.updateResources(delta)
    woodDelay = math.max(woodDelay - delta, Player.maxWoodDelay)
    while woodDelay < 0 do
        Player.addResource('Wood', #baseGlobe.soldiers)
        woodDelay = woodDelay + Player.maxWoodDelay
    end

    if not baseGlobe.left and not baseGlobe.right then
        livesDelay = math.max(livesDelay - delta, Player.maxLivesDelay)
        while livesDelay < 0 do
            Player.addResource('Lives', 1)
            livesDelay = livesDelay + Player.maxLivesDelay
        end
    end
end

function Player.getDisplayResources()
    return { 'Stone', 'Wood', 'Lives' }
end

Player.newResource('Stone')
Player.newResource('Wood')
Player.newResource('Lives', 1)

return Player