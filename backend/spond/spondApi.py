import asyncio
import datetime
import json
from time import sleep
import sys
import base64
from dotenv import load_dotenv
import os
import aiohttp  # Import aiohttp for async HTTP requests
from spond import spond

load_dotenv()

username = os.getenv('SPOND_USERNAME')
password = os.getenv('SPOND_PASSWORD')
group_id = os.getenv('SPOND_GROUP_ID')
api_base_url = os.getenv('URL')  # Fetch the base URL from the environment file
api_spond_private="/api/secure/spondEvents"
api_spond_public="/api/public/spondEvents"

usernameSpring="SpondApi@hotmail.com"
passwordSpring="fdsghrefdgSFGDSF1654dfdsf;,sdjfsdf"
cred=f"{usernameSpring}:{passwordSpring}"
encoded_credentials = base64.b64encode(cred.encode("utf-8")).decode("utf-8")

# Define the headers that will be used in every API call
headers = {
    "Content-Type": "application/json",  # Replace with your actual API key
    "x-api-token":os.getenv('API_KEY'),
}

def add_two_hours_to_timestamp(timestamp):
    # Parse the timestamp to a datetime object, replacing 'Z' with '+00:00'
    dt = datetime.datetime.fromisoformat(timestamp.replace('Z', '+00:00'))
    
    # Add 2 hours
    dt += datetime.timedelta(hours=1)
    
    # Return the formatted timestamp in the 'YYYY-MM-DD HH:MM:SS.0' format
    return dt.strftime('%Y-%m-%d %H:%M:%S.0')

async def get_events_spond():
    #login and getting filtered events
    s = spond.Spond(username=username, password=password)
    group = await s.get_group(group_id)
    events = await s.get_events(group_id=group_id, include_scheduled=True, min_start=datetime.datetime.now())
    await s.clientsession.close()
    eventlist = []

    #repetitie events not allowed because it's not a concert
    for event in events:
        if 'repetitie' not in event['heading'].lower():
            event_details = {
                "spondId": event['id'],
                "title": event['heading'],
                "startTime": add_two_hours_to_timestamp(event['startTimestamp']),
                "endTime": add_two_hours_to_timestamp(event['endTimestamp']),
                "location": event['location']['feature']
            }

            # Add description if it exists
            if 'description' in event:
                event_details['description'] = event['description']

            eventlist.append(event_details)


    return eventlist

#Get events from the DB via endpoint
async def get_local_events():
    url = f"{api_base_url+api_spond_public}"
    async with aiohttp.ClientSession() as session:
        async with session.get(url, headers=headers) as response:
            
            if response.status == 200:
                return await response.json()  # Assuming the response contains the events as JSON
            else:
                print(f"Failed to fetch local events. Status: {response.status}")
                return []

#Delete Spond event from backend where date is overdue, may not work
async def delete_event_from_local(event):
    url = f"{api_base_url+api_spond_private}/spond"
    async with aiohttp.ClientSession() as session:
        async with session.delete(url, json=event, headers=headers) as response:
            
            if response.status == 200:
                print(f"Successfully deleted event with ID {event['id']}")
                return True
            else:
                print(f"Failed to delete event with ID {event['id']}. Status: {response.status}")
                return False

#Add Spond event to backend if spond event doesn't exist in the DB
async def post_event_to_local(event):
    url = f"{api_base_url+api_spond_private}"
    async with aiohttp.ClientSession() as session:
        async with session.post(url, json=event, headers=headers) as response:
            
            if response.status == 200:
                print(f"Successfully added event with ID {event['spondId']}")
                return True
            else:
                print(f"Failed to add event with ID {event['spondId']}. Status: {response.status}")
                return False

#Update Spond event off backend if fiels set by spondEvent don't match with the saved event in DB
async def update_event_in_local(event):
    url = f"{api_base_url+api_spond_private}/spond"
    async with aiohttp.ClientSession() as session:
        async with session.put(url, json=event, headers=headers) as response:
            
            if response.status == 200:
                print(f"Successfully updated event with ID {event['spondId']}")    
                return True           
            else:
                print(f"Failed to update event with ID {event['spondId']}. Status: {response.status}")
                return False


#Compare events and decide which action to persue
async def compare_and_update_events(spond_events, local_events):
    local_events_map = {event['spondId']: event for event in local_events if 'spondId' in event}
    
    # Handle deletions: Events that are in local but not in spond
    for local_event in local_events:
        if 'spondId' in local_event:
            if local_event['spondId'] not in {event['spondId'] for event in spond_events}:
                print(f"Event {local_event['spondId']} is no longer in Spond, deleting locally.")                
                boolean = await delete_event_from_local(local_event)
                if boolean:
                    local_events.remove(local_event)

    # Handle additions and updates: Events in Spond but not local, or different
    for spond_event in spond_events:
        local_event = local_events_map.get(spond_event['spondId'])

        if not local_event:
            print(f"Event {spond_event['spondId']} does not exist locally, adding it.")
            boolean = await post_event_to_local(spond_event)
            if boolean:
                local_events.append(spond_event)
        elif local_event != spond_event:
            print(f"Event {spond_event['spondId']} differs locally, updating it.")
            print(local_event)
            print("----------------------------------------------")
            print(spond_event)
            boolean = await update_event_in_local(spond_event)
            if boolean:
                local_event = spond_event

async def main():
    print("Fetching Spond events and comparing with local events...")
    local_events = []
    firsttime = True
    while True:
        #await asyncio.sleep(60)  # Wait 60 sec for the DB, is for docker
        try:
            if firsttime:
                local_events = await get_local_events()
                spond_events = await get_events_spond()
                await compare_and_update_events(spond_events, local_events)
                firsttime = False
            else:
                spond_events = await get_events_spond()
                await compare_and_update_events(spond_events, local_events)

        except Exception as e:
            print(f"Error occurred: {e}")
            sys.exit(1)  # Exit the script and stop the container

        await asyncio.sleep(5)  # Add a small sleep to prevent tight loops
        print("Done. Sleeping for 24 hours.")
        sleep(24 * 60 * 60)  # Sleep for 24 hours

if __name__ == "__main__":
    asyncio.run(main())
