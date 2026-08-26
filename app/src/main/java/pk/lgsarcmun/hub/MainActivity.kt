package pk.lgsarcmun.hub

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    private val requestCamera = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { HubApp { requestCamera.launch(Manifest.permission.CAMERA) } } }
}

@Composable
fun HubApp(requestCamera: () -> Unit) {
    var tab by remember { mutableIntStateOf(0) }
    MaterialTheme(colorScheme = darkColorScheme(primary = Color(0xFF54B2FF), background = Color(0xFF050A12), surface = Color(0xFF0D1724))) {
        Scaffold(containerColor = Color(0xFF050A12), bottomBar = {
            NavigationBar(containerColor = Color(0xFF08111D)) {
                listOf("Home", "Scan", "Membership", "More").forEachIndexed { i, label ->
                    NavigationBarItem(selected = tab == i, onClick = { tab = i }, icon = { Text(listOf("⌂", "⌁", "▣", "•••")[i], fontSize = 20.sp) }, label = { Text(label) })
                }
            }
        }) { padding ->
            Box(Modifier.padding(padding).fillMaxSize()) {
                when (tab) {
                    0 -> HomeScreen { tab = 1 }
                    1 -> ScanScreen(requestCamera)
                    2 -> MembershipScreen()
                    else -> MoreScreen()
                }
            }
        }
    }
}

@Composable fun Header() { Column(Modifier.padding(22.dp)) { Text("LGSARCMUN", color = Color(0xFF66B9FF), fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp); Text("Hub", color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Bold); Text("Your official society companion", color = Color(0xFF92A5B8), fontSize = 14.sp) } }

@Composable fun HomeScreen(onScan: () -> Unit) { Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0A1B30), Color(0xFF050A12))))){ Header(); Card(Modifier.padding(20.dp).fillMaxWidth(), colors=CardDefaults.cardColors(containerColor=Color(0xFF10253B)), shape=RoundedCornerShape(24.dp)){ Column(Modifier.padding(24.dp)){ Text("Welcome to LGSARCMUN Hub", color=Color.White, fontSize=22.sp, fontWeight=FontWeight.Bold); Text("Membership, identity and society tools — all in one place.", color=Color(0xFFAFC0D1), modifier=Modifier.padding(top=7.dp)); Button(onClick=onScan, modifier=Modifier.fillMaxWidth().padding(top=20.dp), shape=RoundedCornerShape(14.dp)){ Text("SCAN MEMBERSHIP", fontWeight=FontWeight.Bold) } } }; Text("Quick access", color=Color.White, fontWeight=FontWeight.Bold, fontSize=18.sp, modifier=Modifier.padding(22.dp,20.dp,22.dp,12.dp)); Row(Modifier.padding(horizontal=20.dp), horizontalArrangement=Arrangement.spacedBy(12.dp)){ QuickCard("My Membership","Digital ID", Modifier.weight(1f)); QuickCard("Activity","Recent scans", Modifier.weight(1f)) } } }

@Composable fun QuickCard(a:String,b:String, modifier: Modifier){ Card(modifier, shape=RoundedCornerShape(18.dp), colors=CardDefaults.cardColors(containerColor=Color(0xFF0D1724))){ Column(Modifier.padding(16.dp)){ Text(a,color=Color.White,fontWeight=FontWeight.Bold); Text(b,color=Color(0xFF7F93A8),fontSize=12.sp,modifier=Modifier.padding(top=6.dp)) } } }

@Composable fun ScanScreen(requestCamera: () -> Unit) { val context=LocalContext.current; val granted=androidx.core.content.ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED; Column(Modifier.fillMaxSize().background(Color(0xFF050A12)),horizontalAlignment=Alignment.CenterHorizontally){ Text("Scan Membership",color=Color.White,fontSize=28.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=28.dp)); Text("Securely verify an LGSARCMUN QR code",color=Color(0xFF91A4B8),modifier=Modifier.padding(8.dp)); if(granted){ CameraScanner(Modifier.padding(22.dp).fillMaxWidth().aspectRatio(1f)); Text("Align the QR code inside the frame",color=Color(0xFF9CAEC0),fontSize=12.sp) } else { Box(Modifier.padding(30.dp).fillMaxWidth().aspectRatio(1f).background(Color(0xFF0D1724),RoundedCornerShape(28.dp)),contentAlignment=Alignment.Center){ Text("CAMERA ACCESS REQUIRED",color=Color(0xFF66B9FF),fontWeight=FontWeight.Bold) }; Button(onClick=requestCamera,modifier=Modifier.padding(top=10.dp),shape=RoundedCornerShape(14.dp)){ Text("ALLOW CAMERA") } } } }

@Composable fun CameraScanner(modifier: Modifier) { Box(modifier.background(Color.Black,RoundedCornerShape(28.dp)),contentAlignment=Alignment.Center){ Text("QR CAMERA\n\nPosition QR inside frame",color=Color(0xFF66B9FF),fontWeight=FontWeight.Bold) } }
@Composable fun MembershipScreen(){ Column(Modifier.fillMaxSize().background(Color(0xFF050A12))){ Text("My Membership",color=Color.White,fontSize=30.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(24.dp)); Card(Modifier.padding(20.dp).fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=Color(0xFF10253B)),shape=RoundedCornerShape(24.dp)){ Column(Modifier.padding(24.dp)){ Text("YOUR DIGITAL ID",color=Color(0xFF66B9FF),fontSize=11.sp,fontWeight=FontWeight.Bold); Text("Sign in to access your membership card.",color=Color.White,fontSize=19.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=12.dp)); Button(onClick={},modifier=Modifier.fillMaxWidth().padding(top=20.dp)){ Text("SIGN IN / SIGN UP") } } } } }
@Composable fun MoreScreen(){ Column(Modifier.fillMaxSize().background(Color(0xFF050A12))){ Text("More",color=Color.White,fontSize=30.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(24.dp)); listOf("Announcements","Settings","Help & Support","About LGSARCMUN Hub").forEach{ Text(it,color=Color.White,fontSize=17.sp,modifier=Modifier.fillMaxWidth().padding(20.dp)) } } }
