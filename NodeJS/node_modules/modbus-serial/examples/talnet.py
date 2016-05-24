from pyca.ca_tal_tcp import CaTalTCP

client = CaTalTCP(ip = "2.55.72.186", ip_port = 28674)

print client.get_par(0, 1, [1,2,3,4,5,6,7,8,9])



